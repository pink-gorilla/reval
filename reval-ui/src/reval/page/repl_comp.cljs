(ns reval.page.repl-comp
  "Flexlayout component registrations for the repl (side-effect ns)."
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [promesa.core :as p]
   [uix.core :refer [$ defui]]
   [clj-service.http :refer [clj]]
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.dali.viewer.directory-explorer-viewer :refer [directory-explorer-viewer]]
   [reval.dali.viewer.notebook :refer [notebook empty-notebook add-segment]]
   [reval.notebook-ui.editor-tab :as edtab]
   [reval.page.repl-flex :as rflex]
   [layout.flexlayout.comp :refer [component-ui]]
   [ui.codemirror.theme :as theme]
   [ui.codemirror.codemirror :refer [codemirror]]))

(def cm-opts {:lineWrapping false})

(defui welcome-pane [_]
  ($ :div {:style {:padding "24px" :color "#6b7280" :font-size "14px"}}
     "Open a notebook source from the tree on the left. Each file opens in a new tab with the editor on the left and notebook output on the right."))

(defmethod component-ui "reval-repl-welcome" [_opts]
  ($ welcome-pane))

(defn- explorer-inner []
  (fn []
    [directory-explorer-viewer
     {:link rflex/open-file-from-explorer!
      :active-res-path @rflex/explorer-highlight-res-path}]))

(defmethod component-ui "reval-repl-explorer" [_opts]
  ($ :div {:style {:height "100%" :width "100%" :min-height 0 :overflow "hidden"}}
     (r/as-element [explorer-inner])))

(defn- eval-all-local! [m]
  (let [{:keys [editor-id fmt nb-a ext]} m
        fmt-kw (keyword (or ext fmt :clj))]
    (when-let [code (edtab/cm-get-code editor-id)]
      (reset! nb-a (empty-notebook))
      (-> (kernel-eval {:code code :kernel fmt-kw})
          (p/then #(swap! nb-a add-segment %))))))

(defn- eval-segment-local! [m]
  (let [{:keys [editor-id fmt nb-a ext]} m
        fmt-kw (keyword (or ext fmt :clj))]
    (when-let [code (edtab/current-expression editor-id)]
      (-> (kernel-eval {:code code :kernel fmt-kw})
          (p/then #(swap! nb-a add-segment %))))))

(defn- eval-nb-local! [m]
  (let [{:keys [nb-a nbns]} m]
    (when-not (str/blank? (str nbns))
      (-> (clj {:timeout 120000}
               'reval.document.notebook/eval-notebook nbns)
          (p/then (fn [r]
                    (reset! nb-a (:data r))))))))

(defn- save-local! [m]
  (let [{:keys [editor-id path res-path]} m]
    (edtab/save-code! editor-id path res-path)))

(defn- clear-local! [m]
  (when-let [nb-a (:nb-a m)]
    (reset! nb-a (empty-notebook))))

(defn- tab-toolbar [m]
  (let [{:keys [fmt nbns path res-path ext]} m
        fmt (or fmt :clj)
        fmt-l (or ext fmt)]
    [:div {:style {:padding "8px 10px"
                   :background "#f3f4f6"
                   :flex-shrink 0
                   :border-bottom "1px solid #e5e7eb"}}
     [:span {:style {:font-weight 700 :color "#2563eb" :margin-right "12px"}} "repl"]
     [:span {:style {:margin-right "8px"}}
      (if (str/blank? (str res-path))
        (str "ns: " nbns)
        (str "file: " (rflex/filename-only res-path)))]
     [:span {:style {:margin-right "8px" :color "#6b7280"}} (str "format: " fmt-l)]
     [:button {:style {:background "#9ca3af" :margin "2px 4px" :cursor "pointer"}
               :on-click #(eval-all-local! m)}
      "eval all"]
     [:button {:style {:background "#9ca3af" :margin "2px 4px" :cursor "pointer"}
               :on-click #(eval-segment-local! m)}
      "eval current"]
     [:button {:style {:background "#9ca3af" :margin "2px 4px" :cursor "pointer"}
               :on-click #(eval-nb-local! m)}
      "nb eval"]
     [:button {:style {:background "#9ca3af" :margin "2px 4px" :cursor "pointer"
                       :opacity (if (and (str/blank? (str path))
                                         (str/blank? (str res-path)))
                                  0.45 1)}
               :disabled (and (str/blank? (str path)) (str/blank? (str res-path)))
               :on-click #(when (or (not (str/blank? (str path)))
                                    (not (str/blank? (str res-path))))
                            (save-local! m))}
      "save"]
     [:button {:style {:background "#9ca3af" :margin "2px 4px" :cursor "pointer"}
               :on-click #(clear-local! m)}
      "clear output"]]))

(defn- repl-file-panel [opts]
  (let [load-key (r/atom nil)]
    (fn [opts]
      (let [st (:state opts)
            cfg (rflex/config-map (:config opts))
            merged (merge cfg @st)
            {:keys [nb-a editor-id nbns path res-path ext]} merged
            fmt-kw (keyword (or ext :clj))
            k [nbns fmt-kw path res-path]]
        (when (and editor-id (not= @load-key k))
          (reset! load-key k)
          (-> (if (str/blank? (str res-path))
                (clj {:timeout 1000}
                     'reval.document.notebook/load-src nbns fmt-kw)
                (clj {:timeout 1000}
                     'reval.document.notebook/load-src-by-res-path res-path))
              (p/then (fn [src]
                        (js/setTimeout #(edtab/cm-set-code editor-id src) 0)))))
        [:div {:style {:height "100%" :width "100%" :min-height 0
                       :display "flex" :flex-direction "column"}}
         [tab-toolbar merged]
         [:div {:style {:flex 1 :min-height 0 :display "flex" :overflow "hidden"}}
          [:div {:style {:flex 1 :min-width 0 :min-height 0 :display "flex" :flex-direction "column"}}
           [theme/style-codemirror-fullscreen]
           [:div.my-codemirror {:style {:flex 1 :min-height 0 :width "100%"}}
            [codemirror editor-id cm-opts]]]
          [:div {:style {:flex 1 :min-width 0 :min-height 0 :overflow "auto"
                         :background "#dbeafe"}}
           [notebook @nb-a]]]]))))

(defmethod component-ui "reval-repl-file" [opts]
  ($ :div {:style {:height "100%" :width "100%" :min-height 0}}
     (r/as-element [repl-file-panel opts])))

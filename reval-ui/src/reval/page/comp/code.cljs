(ns reval.page.comp.code
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [promesa.core :as p]
   [uix.core :refer [$ defui]] 
   [ui.codemirror.theme :as theme]
   [ui.codemirror.codemirror :refer [codemirror-themed]]
   [layout.flexlayout.comp :refer [component-ui]]
   [clj-service.http :refer [clj]]
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.dali.viewer.notebook :refer [notebook empty-notebook add-segment]]
   [reval.repl.codemirror-tab :as edtab]
   [reval.page.repl-flex :as rflex]
   ))


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
               'reval.notebook/eval-notebook nbns)
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


(def cm-opts {:lineWrapping false})

(defn- repl-code-panel [opts]
  (r/with-let [load-key (r/atom nil)]
    (let [st (:state opts)
          cfg (rflex/config-map (:config opts))
          merged (merge cfg @st)
          {:keys [editor-id nbns path res-path ext]} merged
          fmt-kw (keyword (or ext :clj))
          k [nbns fmt-kw path res-path]]
      (when (and editor-id (not= @load-key k))
        (reset! load-key k)
        (-> (if (str/blank? (str res-path))
              (clj {:timeout 1000}
                   'reval.namespace.store/load-src nbns fmt-kw)
              (clj {:timeout 1000}
                   'reval.namespace.store/load-src-by-res-path res-path))
            (p/then (fn [src]
                      (js/setTimeout #(edtab/cm-set-code editor-id src) 0)))))
      [:div {:style {:height "100%" :width "100%" :min-height 0
                     :display "flex" :flex-direction "column"}}
       [tab-toolbar merged]
       [theme/style-codemirror-fullscreen]
       [:div {:style {:flex 1 :min-height 0 :display "flex" :flex-direction "column"
                      :overflow "hidden"}}
        [:div.my-codemirror {:style {:flex 1 :min-height 0 :width "100%"}}
         [codemirror-themed editor-id cm-opts]]]])))


(defmethod component-ui "reval-repl-code" [opts]
  ($ :div {:style {:height "100%" :width "100%" :min-height 0}}
     (r/as-element [repl-code-panel opts])))
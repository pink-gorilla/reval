(ns reval.page.repl
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]
   [promesa.core :as p]
   [spaces.core]
   [clj-service.http :refer [clj]]
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.kernel.clj-remote] ; side effects
   [reval.dali.viewer.directory-explorer-viewer :refer [directory-explorer-viewer]]
   [reval.dali.viewer.notebook :refer [notebook add-segment empty-notebook]]
   [reval.notebook-ui.editor :as cme]))

(defonce repl-code (r/atom ""))
(defonce cur-fmt (r/atom :clj))
(defonce cur-path (r/atom ""))

(defonce nb-a
  (r/atom (empty-notebook)))

(defn clear []
  (reset! nb-a (empty-notebook)))

;; eval cljs

(defn eval-code [fmt code]
  (let [opts {:code code
              :kernel (keyword fmt)}
        rp (kernel-eval opts)] ; :ns @cur-ns
    (println "eval segment: " opts)
    (p/then rp (fn [er]
                 (.log js/console "eval result: " (pr-str er))
                 (swap! nb-a add-segment er)))))

(defn eval-all [fmt]
  (clear)
  (let [code (cme/cm-get-code)]
    (eval-code fmt code)))

(defn eval-segment [fmt]
  (when-let [code (cme/current-expression)]
    (eval-code fmt code)))

;nb-eval

(defn eval-nb [ns _fmt]
  (clear)
  (let [rp (clj 'reval.document.notebook/eval-notebook ns)]
    (p/then rp (fn [r]
                 (println "notebook eval result: " r)
                 (reset! nb-a (:data r))))))

(rf/reg-event-fx
 :repl/eval-expression
 (fn [_cofx [_ _data]]
   ;(info (str "evaluating repl segment!" data))
   ;(print-position)
   (eval-segment @cur-fmt)
   nil))

;; HEADER

(defn- filename-only [res-path]
  (when-not (str/blank? res-path)
    (let [s (str res-path)
          i (.lastIndexOf s "/")]
      (if (neg? i) s (subs s (inc i))))))

(defn repl-header [nbns fmt path res-path]
  ;(reset! eval/cur-ns nbns)
  (reset! cur-fmt fmt)
  (reset! cur-path path)
  [:div {:style {:padding-top "1.25rem"}}
   [:span {:style {:font-size "1.25rem"
                   :line-height "1.75rem"
                   :color "#3b82f6"
                   :font-weight "700"
                   :margin-right "1rem"}}
    "repl"]
   [:span (if (str/blank? res-path)
            (str "ns: " nbns)
            (str "file: " (filename-only res-path)))
    "  format: " fmt]
   [:button {:style {:background "#9ca3af"
                     :margin "4px"
                     :cursor "pointer"}
             :on-click #(eval-all fmt)}
    "eval all"]
   [:button {:style {:background "#9ca3af"
                     :margin "4px"
                     :cursor "pointer"}
             :on-click #(eval-segment fmt)}
    "eval current"]
   [:button {:style {:background "#9ca3af"
                     :margin "4px"
                     :cursor "pointer"}
             :on-click #(eval-nb nbns fmt)}
    "nb eval"]
   [:button {:style {:background "#9ca3af"
                     :margin "4px"
                     :cursor "pointer"
                     :opacity (if (and (str/blank? (str path))
                                       (str/blank? (str res-path)))
                                0.45
                                1)}
             :disabled (and (str/blank? (str path))
                            (str/blank? (str res-path)))
             :on-click #(when (or (not (str/blank? (str path)))
                                  (not (str/blank? (str res-path))))
                          (cme/save-code path res-path))}
    "save"]
   [:div {:style {:background "#93c5fd"
                  :display "inline-block"}}
    [:button {:style {:background "#9ca3af"
                      :margin "4px"
                      :cursor "pointer"}
              :on-click clear}
     "clear output"]]])

(defn repl-output []
  [:div {:style {:width "100%"
                 :height "100%"
                 :background "#6b7280"}}
   [:div#repltarget]
   [:div {:style {:overflow "scroll"
                  :height "100%"
                  :width "100%"}}
    [notebook @nb-a]]])

(defn editor [_ns _fmt _path _res-path]
  (let [loaded (r/atom [nil nil nil nil])]
    (fn [nbns fmt path res-path]
      (let [comparator [nbns fmt path res-path]]
        (when (not (= comparator @loaded))
          (println "loaded: " @loaded)
          (reset! loaded comparator)
          (-> (if (str/blank? res-path)
                (clj {:timeout 1000}
                     'reval.document.notebook/load-src
                     nbns (keyword fmt))
                (clj {:timeout 1000}
                     'reval.document.notebook/load-src-by-res-path
                     res-path))
              (p/then (fn [result]
                        (println "code result: " result)
                        (reset! repl-code result)
                        (cme/cm-set-code result)
                        ;(swap! editor-id inc)
                        ))))
        [cme/cm-editor]))))

(defn goto-nb [nbinfo]
  (let [qp (cond-> {:nbns (:nbns nbinfo)
                    :ext (:ext nbinfo)}
             (:path nbinfo) (assoc :path (str (:path nbinfo)))
             (:name-full nbinfo) (assoc :res-path (:name-full nbinfo)))]
    (rfe/navigate 'reval.page.repl/repl-page {:query-params qp})))

(defn repl [_opts]
  (fn [{:keys [nbns ext path res-path]
        :or {ext "clj"
             nbns "user"
             path ""
             res-path ""}}]
    [spaces.core/viewport
     [spaces.core/top-resizeable {:size "10%"
                                  :scrollable false
                                  :style {:background "#f3f4f6"}}
      [repl-header nbns ext path res-path]]
     [spaces.core/fill {}
      [spaces.core/left-resizeable {:size "10%"
                                    :scrollable true
                                    :style {:background "#f3f4f6"
                                            :max-height "100%"
                                            :overflow-y "auto"}}
       [directory-explorer-viewer
        {:link goto-nb
         :active-res-path res-path}]]
      [spaces.core/fill {}
       [editor nbns ext path res-path]] ; [:div.h-full.w-full.bg-blue-900.max-h-full.overflow-y-auto]
      [spaces.core/right-resizeable {:size "50%"
                                     :scrollable true
                                     :style {:background "#dbeafe"
                                             :max-height "100%"
                                             :overflow-y "auto"}}
       [repl-output]]]]))

(defn repl-page [{:keys [_route-params query-params _handler] :as _route}]
  [repl query-params])

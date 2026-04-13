(ns reval.page.comp.notebook
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [uix.core :refer [$ defui]]
   [layout.flexlayout.comp :refer [component-ui]]
   [reval.dali.viewer.notebook :refer [notebook empty-notebook add-segment]]
   [reval.page.repl-flex :as rflex]
   [shadowx.core :refer [get-resource-path]]
   [reval.namespace.path :refer [ns->dir]]
   [dali.transform.load :refer [load-edn]]
   [dali.hooks :refer [use-effect]]))

(defn url-notebook [nbns]
  ; target/webly/public/rdocument/notebook/study/movies/notebook.edn
  ; http://localhost:8080/r/rdocument/notebook/study/movies/notebook.edn
  (if nbns
    (str (get-resource-path) "rdocument/" (ns->dir nbns) "/notebook.edn")
    (str (get-resource-path) "rdocument/welcome.edn")))

(defn dynamic-notebook [nb-a]
  [notebook @nb-a])

(defui notebook-ui [opts]
  (let [st (:state opts)
        cfg (rflex/config-map (:config opts))
        merged (merge cfg @st)
        nb-a (:nb-a merged)
        nbns (:nbns cfg)]
    (use-effect (fn []
                  (-> (load-edn {:url (url-notebook nbns)})
                      (p/then (fn [nb]
                                (reset! nb-a nb)))))
                #js [nbns])
    (r/as-element  [dynamic-notebook nb-a])))

(defmethod component-ui "reval-repl-notebook" [opts]
  ($ :div {:style {:height "100%" :width "100%" :min-height 0
                   :overflow "auto" :background "#dbeafe"}}
     ($ notebook-ui opts)))

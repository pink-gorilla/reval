(ns reval.page.comp.notebook
  (:require
   [reagent.core :as r]
   [uix.core :refer [$ defui]]
   [layout.flexlayout.comp :refer [component-ui]]
   [reval.dali.viewer.notebook :refer [notebook empty-notebook add-segment]]
   [reval.page.repl-flex :as rflex]
   ))


(defn- repl-notebook-panel [_]
  (fn [opts]
    (let [st (:state opts)
          cfg (rflex/config-map (:config opts))
          merged (merge cfg @st)
          nb-a (:nb-a merged)]
      [:div {:style {:height "100%" :width "100%" :min-height 0
                     :overflow "auto"
                     :background "#dbeafe"}}
       [notebook @nb-a]])))


(defmethod component-ui "reval-repl-notebook" [opts]
  ($ :div {:style {:height "100%" :width "100%" :min-height 0}}
     (r/as-element [repl-notebook-panel opts])))

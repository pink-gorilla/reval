(ns reval.page.comp.explorer
  (:require
    [reagent.core :as r]
    [uix.core :refer [$ defui]]
    [layout.flexlayout.comp :refer [component-ui]]
    [reval.repl.directory-explorer :refer [directory-explorer-ui]]
    [reval.page.repl-flex :as rflex]
   ))





(defn- explorer-inner []
  (fn []
    [directory-explorer-ui
     {:link rflex/open-file-from-explorer!
      :active-res-path @rflex/explorer-highlight-res-path}]))

(defmethod component-ui "reval-repl-explorer" [_opts]
  ($ :div {:style {:height "100%" :width "100%" :min-height 0 :overflow "hidden"}}
     (r/as-element [explorer-inner])))
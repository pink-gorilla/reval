(ns reval.page.comp.welcome
  (:require
   [uix.core :refer [$ defui]]
   [layout.flexlayout.comp :refer [component-ui]]))

(defui welcome-pane [_]
  ($ :div {:style {:padding "24px" :color "#6b7280" :font-size "14px"}}
     "Open a notebook source from the tree on the left. Each file is one tab with code and output inside it; closing that tab removes both. Resize the splitter between editor and results as needed."))

(defmethod component-ui "reval-repl-welcome" [_opts]
  ($ welcome-pane))
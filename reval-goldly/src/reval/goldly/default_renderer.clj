(ns reval.goldly.default-renderer
  (:require
   [scicloj.kindly.goldly :refer [add-renderer]]))

(add-renderer :kind/vega 'ui.vega/vega)
(add-renderer :kind/vegalite 'ui.vega/vegalite)
(add-renderer :kind/code 'ui.highlightjs/highlightjs)



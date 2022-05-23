(ns reval.goldly.default-renderer
  (:require
   [scicloj.kindly.goldly :refer [add-renderer]]))

(add-renderer :kind/vega 'user/vega)
(add-renderer :kind/vega-lite 'user/vega-lite)
(add-renderer :kind/code 'user/highlightjs)



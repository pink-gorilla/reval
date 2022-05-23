(ns scicloj.kindly.goldly
  (:require
   [reval.goldly.vizspec :refer [render-value-with]]
   [scicloj.kindly.v2.generate :refer [generate-kind!]]))

(defn add-renderer [kind escaped-sci-cljs-render-fn]
  (generate-kind!
   kind
   (render-value-with escaped-sci-cljs-render-fn)))
(ns reval.goldly.vizspec)

(defn render-value-with [escaped-sci-cljs-render-fn]
  (fn [v]
    (with-meta
      [escaped-sci-cljs-render-fn v]
      {:R true})))
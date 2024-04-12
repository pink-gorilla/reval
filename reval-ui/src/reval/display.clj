(ns reval.display)

(defn render-value-with [escaped-sci-cljs-render-fn]
  (fn [v]
    (with-meta
      [escaped-sci-cljs-render-fn v]
      {:R true})))

(defn render-fn [t v]
  (with-meta
    {:data v}
    {:render-fn-escaped t}))

(defn reagent [v]
  (with-meta
    v
    {:reagent true}))

(defn hiccup [v]
  (with-meta
    v
    {:hiccup true}))




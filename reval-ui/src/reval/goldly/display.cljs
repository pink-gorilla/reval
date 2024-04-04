(ns reval.goldly.display)

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





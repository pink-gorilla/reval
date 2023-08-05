(ns reval.goldly.viz.show
  (:require
   [reval.goldly.viz.render :refer [get-render-fn]]
   [viz.hiccup :refer [resolve-hiccup]]
   [reval.type.converter :as type-converter]))

(defn log [& args]
  (.log js/console (apply str args)))

(defn show-data [s v]
  (log "show-data symbol: " s " val: " v)
  (let [render-fn (get-render-fn s)]
    (fn [s v]
      [render-fn v])))


(ns reval.viz.show
  (:require
   [reval.viz.render :refer [get-render-fn]]))

(defn log [& args]
  (.log js/console (apply str args)))

(defn show-data [s v]
  (fn [s v]
    (log "show-data symbol: " s " val: " v)
    (let [render-fn (get-render-fn s)]
      (if render-fn
        [render-fn v]
        [:p "unknown render-fn: " (str s)]))))

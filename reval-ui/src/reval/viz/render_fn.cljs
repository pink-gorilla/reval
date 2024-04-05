(ns reval.viz.render-fn
  (:require
   [viz.hiccup :refer [resolve-hiccup]]
   [reval.viz.render :refer [get-render-fn]]))

(defn hiccup [data]
  data)

(defn reagent [data]
  (resolve-hiccup get-render-fn data))







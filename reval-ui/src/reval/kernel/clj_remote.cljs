(ns reval.kernel.clj-remote
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [taoensso.timbre :refer [debug info warnf error]]
   [goldly.service.core :refer [clj]]
   [reval.kernel.protocol :refer [kernel-eval]]))

(defonce cur-ns (r/atom "user"))

(defn eval-clj [segment]
  (let [segment (merge segment {:ns @cur-ns})
        _ (info "eval clj: " segment)
        rp (clj {:timeout 120000} 'reval.viz.eval/viz-eval-blocking segment)]
    (p/then rp (fn [r]
                 (info "eval clj result: " r)
                 (reset! cur-ns (:ns r))))
    rp))

(defmethod kernel-eval :clj [seg]
  (eval-clj seg))

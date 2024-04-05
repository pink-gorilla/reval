(ns reval.kernel.clj
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [goldly.service.core :as service]))

(defonce cur-ns (r/atom "user"))

(defn eval-clj [on-evalresult opts]
  (let [opts (merge opts {:ns @cur-ns})]
    (service/run-cb
     {:fun 'reval.viz.eval/viz-eval
      :args [opts]
      :timeout 60000
      :cb (fn [[s {:keys [result]}]]
            (let [{:keys [ns]} result]
              ;(println "clj-eval result: " result)
              (on-evalresult result)
               ;(reset! clj-er {:er result})
               ;(println "setting ns to: " ns)
              (reset! cur-ns ns)))})))

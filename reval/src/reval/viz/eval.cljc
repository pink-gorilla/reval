(ns reval.viz.eval
  (:require
   [promesa.core :as p]
   [taoensso.timbre :refer [debug info warnf error]]
   [reval.viz.data :refer [value->data]]
   [reval.kernel.protocol :refer [kernel-eval]]
   #?(:clj [reval.kernel.clj-eval]) ; side-effects
   ))

(defn viz-eval [segment]
  (info "viz-eval segment: " segment)
  (let [eval-p (kernel-eval segment)]
    (p/then eval-p (fn [{:keys [err value] :as er}]
                     (if err
                       er
                       (->  er
                            (dissoc :value)
                            (merge  (value->data value))))))))

(defn viz-eval-blocking [segment]
  (p/await (viz-eval segment)))

(comment
  (-> (viz-eval {:code "(/ 1 3)" :ns "user" :kernel :clj})
      (p/then (fn [r] (println "eval result: " r))))

  (-> (viz-eval {:code "(+ 1 3)" :ns "user" :kernel :clj})
      (p/then (fn [r] (println "eval result: " r))))

  (viz-eval-blocking {:code "(+ 1 3)" :ns "user" :kernel :clj})

;
  )

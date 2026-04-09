(ns reval.kernel.dali
  (:require
   [promesa.core :as p]
   [taoensso.timbre :refer [info]]
   [dali.plot.exception :refer [exception]]
   [reval.type.converter :refer [type->dali]]
   [reval.kernel.protocol :refer [kernel-eval]]
   #?(:clj [reval.kernel.clj-eval]) ; side-effects
   ))
(defn dalify [{:keys [ex value] :as er}]
  (cond
    ; exception -> dali spec
    ex (-> er
           (dissoc :ex)
           (assoc :err (exception ex)))
       ; type -> dali spec
    :else
    (-> er
        (dissoc :value)
        (assoc :result (type->dali value)))))

(defn dali-eval [segment]
  (info "viz-eval segment: " segment)
  (let [eval-p (kernel-eval segment)]
    (p/then eval-p dalify)))

#?(:clj
   (defn dali-eval-blocking [segment]
     (p/await (dali-eval segment))))

(comment
  (-> (dali-eval  {:code "(/ 1 3)" :ns "user" :kernel :clj})
      (p/then (fn [r] (println "eval result: " r))))

  (-> (dali-eval  {:code "(+ 1 3)" :ns "user" :kernel :clj})
      (p/then (fn [r] (println "eval result: " r))))

  (dali-eval-blocking  {:code "(+ 1 3)" :ns "user" :kernel :clj})

;
  )

(ns reval.dali.eval
  (:require
   [promesa.core :as p]
   [taoensso.timbre :refer [info]]
   [dali.plot.exception :as plot]
   [reval.type.converter :refer [type->dali]]
   [reval.kernel.protocol :refer [kernel-eval]]
   #?(:clj [reval.kernel.clj-eval]) ; side-effects
   ))

(defn dalify [env {:keys [ex value] :as er}]
   (if ex
     (-> er
        (dissoc :ex)
        (assoc :err (plot/exception "" ex)))
     (-> er
         (dissoc :value)
         (assoc :result (type->dali env value)))))

(defn dali-eval [env segment]
  (info "viz-eval segment: " segment)
  (let [eval-p (kernel-eval segment)]
    (p/then eval-p #(dalify env %))))

 #?(:clj
(defn dali-eval-blocking [env segment]
  (p/await (dali-eval env segment)))
 )

(comment
  (-> (dali-eval nil {:code "(/ 1 3)" :ns "user" :kernel :clj})
      (p/then (fn [r] (println "eval result: " r))))

  (-> (dali-eval nil {:code "(+ 1 3)" :ns "user" :kernel :clj})
      (p/then (fn [r] (println "eval result: " r))))

  (dali-eval-blocking nil {:code "(+ 1 3)" :ns "user" :kernel :clj})

;
  )

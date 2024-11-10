(ns reval.type.converter
  (:require
   [reval.type.protocol :refer [dali-convertable to-dali]]
   [taoensso.timbre :as timbre :refer [info]]
   [reval.dali.plot.type :as plot]))

#?(:clj
   (defn type->dali [env v] ; here env is first
     (try
       (if (satisfies? dali-convertable v)
         (to-dali v env) ; note that for the protocol the type needs to be first. 
         (plot/unknown-type v))
       (catch Exception ex
         (info "v: " v " ex: " ex)
         (plot/type-convert-err v))))

   :cljs
   (defn type->dali [env v] ; here env is first
     (try
       (to-dali v env) ; note that for the protocol the type needs to be first.
       (catch js/Exception _
         (plot/unknown-type v)))))


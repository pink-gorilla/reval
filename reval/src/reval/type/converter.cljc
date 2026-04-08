(ns reval.type.converter
  (:require
   [reval.type.protocol :refer [dali-convertable to-dali]]
   [reval.dali.plot.type :as plot]))

#?(:clj
   (defn type->dali [v] ; here env is first
     (try
       (if (satisfies? dali-convertable v)
         (to-dali v) ; note that for the protocol the type needs to be first. 
         (plot/unknown-type v))
       (catch Exception ex
         (plot/type-convert-err v))))

   :cljs
   (defn type->dali [v] 
     (try
       (to-dali v)
       (catch js/Exception _
         (plot/unknown-type v)))))


(ns reval.default
  "default ns to load ns with side effects"
  (:require

   ; to-dali converters   
   [reval.type.clj] ; side-effects!
   [reval.type.image] ; side-effects!


   ;  kernel 
   [reval.kernel.protocol]))
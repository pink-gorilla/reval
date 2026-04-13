(ns reval.default
  "default ns to load ns with side effects"
  (:require
   ; to-dali converters   
   [dali.type.clj] ; side-effects!
   [dali.type.image] ; side-effects!
   ;  kernel 
   [reval.kernel.protocol]))
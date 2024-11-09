(ns reval.default
  "default ns to load ns with side effects"
  (:require

   ; to-dali converters   
   [reval.type.clj] ; side-effects!
   [reval.type.image] ; side-effects!

   ; storage formats
   [modular.persist.unknown]
   [modular.persist.edn]
   [modular.persist.image]
   [modular.persist.text]

   ;  kernel 
   [reval.kernel.protocol]))
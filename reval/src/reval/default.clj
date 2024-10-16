(ns reval.default
  "default ns to load ns with side effects"
  (:require
     ; render
   ; #?(:clj [picasso.render.clj-types])
   ; #?(:cljs [picasso.render.cljs-types])
   ;#?(:clj [picasso.render.image])

   ; to-hiccup converters   
   [reval.type.clj] ; side-effects!
   [reval.type.image] ; side-effects!

   ; storage formats
   [modular.persist.unknown]
   [modular.persist.edn]
   [modular.persist.image]
   [modular.persist.text]

   ;  kernel 
   [reval.kernel.protocol]

   ; repl functions
   [reval.ui])

  (:import [reval.type.image imgrecord]))
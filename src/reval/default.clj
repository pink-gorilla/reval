(ns reval.default
  "default ns to load ns with side effects"
  (:require
   ;[picasso.protocols]

   ; render
   ; #?(:clj [picasso.render.clj-types])
   ; #?(:cljs [picasso.render.cljs-types])
   ;#?(:clj [picasso.render.image])

   ; to-hiccup converters   
   [reval.type.clj] ; side-effects!
   [reval.type.image] ; side-effects!

   ; storage formats
   [reval.persist.unknown]
   [reval.persist.edn]
   [reval.persist.image]
   [reval.persist.text]))
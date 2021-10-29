(ns reval.default
  "default ns to load ns with side effects"
  (:require
   ;[picasso.protocols]

   ; render
   ; #?(:clj [picasso.render.clj-types])
   ; #?(:cljs [picasso.render.cljs-types])
   ;#?(:clj [picasso.render.image])

   [reval.persist.unknown]
   [reval.persist.edn]
   [reval.persist.image]
   [reval.persist.text]

   [reval.type.clj] ; side-effects!
   [reval.type.image] ; side-effects!
   ))
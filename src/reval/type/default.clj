(ns reval.type.default
  "side effects for Renderable types"
  (:require
   ;[picasso.protocols]

   ; render
   ; #?(:clj [picasso.render.clj-types])
   ; #?(:cljs [picasso.render.cljs-types])
   ;#?(:clj [picasso.render.image])

   [reval.type.clj] ; side-effects!
   [reval.type.image] ; side-effects!
   ))
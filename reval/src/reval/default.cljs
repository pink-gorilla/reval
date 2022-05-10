(ns reval.default
  "default ns to load ns with side effects"
  (:require
   ; to-hiccup converters   
   [reval.type.cljs] ; side-effects!
   [reval.type.sci] ; side-effects!
   ))
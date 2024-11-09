(ns reval.type.sci
  "converts sci values to hiccup representation"
  (:require
   [sci.lang]
   [reval.type.protocol :refer [dali-convertable]]
   [reval.dali.plot.type :refer [simplevalue->dali list->dali map->dali unknown-type]]))

; this cljs file that gets executed in cljs (NOT BY SCI)

#_(extend-type sci.impl.vars/SciVar
    dali-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-symbol")))

#_(extend-type sci.impl.vars.SciNamespace
    dali-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-namespace")))

#_(extend-type sci.impl.vars/SciVar
    dali-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-symbol")))

#_(extend-type sci.impl.vars.SciNamespace
    dali-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-namespace")))

(extend-type sci.lang/Var
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-symbol")))



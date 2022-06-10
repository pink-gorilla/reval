(ns reval.type.sci
  "converts sci values to hiccup representation"
  (:require
   ;[sci.impl.vars]
   [reval.type.protocol :refer [hiccup-convertable #_to-hiccup]]
   [reval.type.ui.simplevalue :refer [simplevalue->hiccup]]
   [reval.type.ui.list :refer [list->hiccup map->hiccup]]))

#_(extend-type sci.impl.vars/SciVar
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-symbol")))

#_(extend-type sci.impl.vars.SciNamespace
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-namespace")))



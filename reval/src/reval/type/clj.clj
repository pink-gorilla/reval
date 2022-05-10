(ns reval.type.clj
  "converts clojure values to hiccup representation"
  (:require
   [reval.type.protocol :refer [hiccup-convertable #_to-hiccup]]
   [reval.type.ui.simplevalue :refer [simplevalue->hiccup]]
   [reval.type.ui.list :refer [list->hiccup map->hiccup]]))

;; Renderers for basic Clojure forms **

;; A default, catch-all renderer that takes anything we don't know what to do with

; everything is a type. not sure if we want to have this
; we now have nil checker. so shoudl be better ?
#_(extend-type Object
    hiccup-convertable
    (to-hiccup [self]
    ; no logging in production!
    ;this fucks up nrepl and then 
    ;(warnf "rendering unknown clj type: %s data: %s" (type self) (pr-str self))
      (simplevalue->hiccup self "clj-unknown")))

;; nil values are a distinct thing of their own
(extend-type nil
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-nil")))

(extend-type Boolean
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-boolean")))

(extend-type clojure.lang.Symbol
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-symbol")))

(extend-type clojure.lang.Namespace
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-namespace")))

(extend-type clojure.lang.Keyword
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-keyword")))

(extend-type clojure.lang.Var
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-var")))

(extend-type clojure.lang.Atom
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-atom")))

(extend-type clojure.lang.Agent
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-agent")))

(extend-type clojure.lang.Ref
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-ref")))

(extend-type java.lang.Class
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-class")))

;; primitive types

(extend-type java.lang.Character
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-char")))

(extend-type java.lang.String
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-string")))

(extend-type java.lang.Integer
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-int")))

(extend-type java.lang.Long
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-long")))

(extend-type clojure.lang.BigInt
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-bigint")))

(extend-type java.lang.Float
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-float")))

(extend-type java.lang.Double
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-double")))

(extend-type java.math.BigDecimal
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-bigdecimal")))

(extend-type clojure.lang.Ratio
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-ratio")))

;; time

(extend-type java.time.LocalDate
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-localdate")))

;; renderers for collection of items

(extend-type clojure.lang.IPersistentVector
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-vector"
      :open "["
      :close "]"
      :separator " "}
     self)))

(extend-type clojure.lang.LazySeq
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-lazy-seq"
      :open "("
      :close ")"
      :separator " "}
     self)))

(extend-type clojure.lang.IPersistentList
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-list"
      :open "("
      :close ")"
      :separator " "}
     self)))

;; TODO: is this really necessary? Is there some interface I'm missing for lists? Or would just ISeq work?
(extend-type clojure.lang.ArraySeq
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-list"
      :open "("
      :close ")"
      :separator " "}
     self)))

(extend-type clojure.lang.Cons
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-list"
      :open "("
      :close ")"
      :separator " "}
     self)))

(extend-type clojure.lang.IPersistentSet
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class  "clj-set"
      :open "#{"
      :close "}"
      :separator " "}
     self)))

;; When we render a map we will map over its entries, which will yield key-value pairs represented as vectors. To render
;; the map we render each of these key-value pairs with this helper function. They are rendered as list-likes with no
;; bracketing. These will then be assembled in to a list-like for the whole map by the IPersistentMap render function.

(extend-type clojure.lang.IPersistentMap
  hiccup-convertable
  (to-hiccup [self]
    (map->hiccup
     {:class  "clj-map"
      :open "{"
      :close "}"
      :separator " "}
     self)))

;; A record is like a map, but it is tagged with its type
(extend-type clojure.lang.IRecord
  hiccup-convertable
  (to-hiccup [self]
    (map->hiccup
     {:class  "clj-record"
      :open "{"
      :close "}"
      :separator " "
      :type (str "#" (pr-str (type self)))}
     self)))
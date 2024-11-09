(ns reval.type.clj
  "converts clojure values to hiccup representation"
  (:require
   [dali.spec :refer [dali-spec?]]
   [reval.dali.plot.type :refer [simplevalue->dali list->dali map->dali]]
   [reval.type.protocol :refer [dali-convertable]]))

;; Renderers for basic Clojure forms **

;; A default, catch-all renderer that takes anything we don't know what to do with

; everything is a type. not sure if we want to have this
; we now have nil checker. so shoudl be better ?
#_(extend-type Object
    dali-convertable
    (to-dali [v env]
    ; no logging in production!
    ;this fucks up nrepl and then 
    ;(warnf "rendering unknown clj type: %s data: %s" (type self) (pr-str self))
      (simplevalue->dali v "clj-unknown")))

;; nil values are a distinct thing of their own
(extend-type nil
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-nil")))

(extend-type Boolean
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-boolean")))

(extend-type clojure.lang.Symbol
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-symbol")))

(extend-type clojure.lang.Namespace
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-namespace")))

(extend-type clojure.lang.Keyword
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-keyword")))

(extend-type clojure.lang.Var
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-var")))

(extend-type clojure.lang.Atom
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-atom")))

(extend-type clojure.lang.Agent
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-agent")))

(extend-type clojure.lang.Ref
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-ref")))

(extend-type java.lang.Class
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-class")))

;; primitive types

(extend-type java.lang.Character
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-char")))

(extend-type java.lang.String
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-string")))

(extend-type java.lang.Integer
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-int")))

(extend-type java.lang.Long
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-long")))

(extend-type clojure.lang.BigInt
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-bigint")))

(extend-type java.lang.Float
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-float")))

(extend-type java.lang.Double
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-double")))

(extend-type java.math.BigDecimal
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-bigdecimal")))

(extend-type clojure.lang.Ratio
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-ratio")))

;; time

(extend-type java.time.LocalDate
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-localdate")))

;; renderers for collection of items

(extend-type clojure.lang.IPersistentVector
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-vector"
      :open "["
      :close "]"
      :separator " "}
     v)))

(extend-type clojure.lang.LazySeq
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-lazy-seq"
      :open "("
      :close ")"
      :separator " "}
     v)))

(extend-type clojure.lang.IPersistentList
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-list"
      :open "("
      :close ")"
      :separator " "}
     v)))

;; TODO: is this really necessary? Is there some interface I'm missing for lists? Or would just ISeq work?
(extend-type clojure.lang.ArraySeq
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-list"
      :open "("
      :close ")"
      :separator " "}
     v)))

(extend-type clojure.lang.Cons
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-list"
      :open "("
      :close ")"
      :separator " "}
     v)))

(extend-type clojure.lang.LongRange
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-list"
      :open "("
      :close ")"
      :separator " "}
     v)))

(extend-type clojure.lang.IPersistentSet
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class  "clj-set"
      :open "#{"
      :close "}"
      :separator " "}
     v)))

;; When we render a map we will map over its entries, which will yield key-value pairs represented as vectors. To render
;; the map we render each of these key-value pairs with this helper function. They are rendered as list-likes with no
;; bracketing. These will then be assembled in to a list-like for the whole map by the IPersistentMap render function.

(extend-type clojure.lang.IPersistentMap
  dali-convertable
  (to-dali [v env]
    (if (dali-spec? v)
      v
      (map->dali
       env
       {:class  "clj-map"
        :open "{"
        :close "}"
        :separator " "}
       v))))

;; A record is like a map, but it is tagged with its type
(extend-type clojure.lang.IRecord
  dali-convertable
  (to-dali [v env]
    (map->dali
     env
     {:class  "clj-record"
      :open "{"
      :close "}"
      :separator " "
      :type (str "#" (pr-str (type v)))}
     v)))
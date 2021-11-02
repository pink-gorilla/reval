(ns reval.type.clj
  "converts clojure values to html representation"
  (:require
   ;[taoensso.timbre :refer [warnf errorf]]
   [reval.type.protocol :refer [hiccup-convertable to-hiccup]]
   ;[picasso.render.span :refer [span-render]]
   ;[picasso.render.list-like :refer [list-like-render list-like-render-map]]
   ))

;; Renderers for basic Clojure forms **

;; A default, catch-all renderer that takes anything we don't know what to do with

(def styles
  {"clj-raw"    {:color "red"}
   "clj-nil"    {:color "grey"}
   "clj-symbol" {:color  "steelblue"}
   "clj-namespace" {:color "steelblue"}
   "clj-keyword" {:color "rgb(30, 30, 82)"}
   "clj-var" {:color "deeppink"}
   "clj-atom" {:color "darkorange"}
   "clj-agent" {:color "darkorange"}
   "clj-ref" {:color "darkorange"}

   "clj-char" {:color "dimgrey"}
   "clj-string" {:color "grey"}
   "clj-int" {:color "blue"}
   "clj-long" {:color "blue"}
   "clj-bigint" {:color "blue"}
   "clj-float" {:color "darkgreen"}
   "clj-double" {:color "darkgreen"}
   "clj-bigdecimal" {:color "darkgreen"}
   "clj-ratio" {:color "darkgreen"}

   "clj-localdate" {:color "green"}})

(defn class->style [c]
  (if-let [s (get styles c)]
    {:style s}
    {:class c}))

(defn span-render
  [thing class]
  [:span (class->style class) (pr-str thing)])

; everything is a type. not sure if we want to have this
; we now have nil checker. so shoudl be better ?
#_(extend-type Object
    hiccup-convertable
    (to-hiccup [self]
    ; no logging in production!
    ;this fucks up nrepl and then 
    ;(warnf "rendering unknown clj type: %s data: %s" (type self) (pr-str self))
      (span-render self "clj-unknown")))

;; nil values are a distinct thing of their own
(extend-type nil
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-nil")))

(extend-type Boolean
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-boolean")))

(extend-type clojure.lang.Symbol
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-symbol")))

(extend-type clojure.lang.Namespace
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-namespace")))

(extend-type clojure.lang.Keyword
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-keyword")))

(extend-type clojure.lang.Var
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-var")))

(extend-type clojure.lang.Atom
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-atom")))

(extend-type clojure.lang.Agent
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-agent")))

(extend-type clojure.lang.Ref
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-ref")))

(extend-type java.lang.Class
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-class")))

;; primitive types

(extend-type java.lang.Character
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-char")))

(extend-type java.lang.String
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-string")))

(extend-type java.lang.Integer
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-int")))

(extend-type java.lang.Long
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-long")))

(extend-type clojure.lang.BigInt
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-bigint")))

(extend-type java.lang.Float
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-float")))

(extend-type java.lang.Double
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-double")))

(extend-type java.math.BigDecimal
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-bigdecimal")))

(extend-type clojure.lang.Ratio
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-ratio")))

;; time

(extend-type java.time.LocalDate
  hiccup-convertable
  (to-hiccup [self]
    (span-render self "clj-localdate")))

;; renderers for collection of items

#_(extend-type clojure.lang.IPersistentVector
    hiccup-convertable
    (to-hiccup [self]
      (list-like-render
       {:class "clj-vector"
        :open "["
        :close "]"
        :separator " "}
       self)))

#_(extend-type clojure.lang.LazySeq
    hiccup-convertable
    (to-hiccup [self]
      (list-like-render
       {:class "clj-lazy-seq"
        :open "("
        :close ")"
        :separator " "}
       self)))

#_(extend-type clojure.lang.IPersistentList
    hiccup-convertable
    (to-hiccup [self]
      (list-like-render
       {:class "clj-list"
        :open "("
        :close ")"
        :separator " "}
       self)))

;; TODO: is this really necessary? Is there some interface I'm missing for lists? Or would just ISeq work?
#_(extend-type clojure.lang.ArraySeq
    hiccup-convertable
    (to-hiccup [self]
      (list-like-render
       {:class "clj-list"
        :open "("
        :close ")"
        :separator " "}
       self)))

#_(extend-type clojure.lang.Cons
    hiccup-convertable
    (to-hiccup [self]
      (list-like-render
       {:class "clj-list"
        :open "("
        :close ")"
        :separator " "}
       self)))

;; When we render a map we will map over its entries, which will yield key-value pairs represented as vectors. To render
;; the map we render each of these key-value pairs with this helper function. They are rendered as list-likes with no
;; bracketing. These will then be assembled in to a list-like for the whole map by the IPersistentMap render function.

#_(extend-type clojure.lang.IPersistentMap
    hiccup-convertable
    (to-hiccup [self]
      (list-like-render-map
       {:class  "clj-map"
        :open "{"
        :close "}"
        :separator " "}
       self)))

#_(extend-type clojure.lang.IPersistentSet
    hiccup-convertable
    (to-hiccup [self]
      (list-like-render
       {:class  "clj-set"
        :open "#{"
        :close "}"
        :separator " "}
       self)))

;; A record is like a map, but it is tagged with its type
#_(extend-type clojure.lang.IRecord
    hiccup-convertable
    (to-hiccup [self]
      (list-like-render-map
       {:class  "clj-record"
        :open "{"
        :close "}"
        :separator " "
        :type (str "#" (pr-str (type self)))}
       self)))
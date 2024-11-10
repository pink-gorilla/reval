(ns reval.type.cljs
  "converts clojurescript values to hiccup representation"
  (:require
   [dali.spec :refer [dali-spec?]]
   [reval.dali.plot.type :refer [simplevalue->dali list->dali map->dali unknown-type]]
   [reval.type.protocol :refer [dali-convertable]]))

;;; ** to-hiccupers for basic Clojure forms **

;; A default, catch-all to-hiccuper that takes anything we don't know what to do with and calls str on it.

;; https://grokbase.com/t/gg/clojure/121d2w4vhn/is-this-a-bug-extending-protocol-on-js-object
;; david nolan:
;; You should never extend js/Object.
;; It's unfortunate since this means we can't currently use js/Object to
;; provide default protocol implementations as we do in Clojure w/o fear of
;; conflicts with JavaScript libraries.

#_(extend-type js/Object
    hiccup-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-unknown")))

(extend-type default
  dali-convertable
  (to-dali [v env]
    (unknown-type v)))

; nil values are a distinct thing of their own

(extend-type nil
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-nil")))

(extend-type cljs.core/Keyword
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-keyword")))

(extend-type cljs.core/Symbol
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-symbol")))

; would be cool to be able to use meta data to switch between
; if meta ^:br is set, then convert \n to [:br] otherwise to-hiccup the string as it is.
; however clojure does not support meta data for strings
(extend-type string
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-string")))

#_(extend-type char
    dali-convertable
    (to-dali [v env]
      (simplevalue->dali v "clj-char")))

(extend-type number
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-long")))

(extend-type boolean
  dali-convertable
  (to-dali [v env]
    (simplevalue->dali v "clj-boolean")))

;; LIST

(extend-type cljs.core/MapEntry
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-vector"
      :open "["
      :close "]"
      :separator " "}
     v)))

(extend-type cljs.core/LazySeq
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-lazy-seq"
      :open  "("
      :close ")"
      :separator " "}
     v)))

(extend-type cljs.core/IntegerRange
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-int-range"
      :open  "("
      :close ")"
      :separator " "}
     v)))

(extend-type cljs.core/PersistentVector
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-vector"
      :open "["
      :close  "]"
      :separator " "}
     v)))

(extend-type cljs.core/List
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-list"
      :open "("
      :close  ")"
      :separator " "}
     v)))

(extend-type cljs.core/PersistentHashSet
  dali-convertable
  (to-dali [v env]
    (list->dali
     env
     {:class "clj-set"
      :open "#{"
      :close  "}"
      :separator  " "}
     v)))

;; MAPS

;; When we to-hiccup a map we will map over its entries, which will yield key-value pairs represented as vectors. To to-hiccup
;; the map we to-hiccup each of these key-value pairs with this helper function. They are to-hiccuped as list-likes with no
;; bracketing. These will then be assembled in to a list-like for the whole map by the IPersistentMap to-hiccup function.

(extend-type cljs.core/PersistentArrayMap
  dali-convertable
  (to-dali [v env]
    (if (dali-spec? v)
      v
      (map->dali
       env
       {:class "clj-map"
        :open "{"
        :close  "}"
        :separator  " "}
       v))))

;; This still needs to be implemented:
;; cljs.core/Range
;; cljs.core/Var
;; cljs.core/List
;; cljs.core/PersistentTreeMap


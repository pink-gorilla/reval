(ns reval.type.cljs
  "converts clojurescript values to hiccup representation"
  (:require
   [reval.type.protocol :refer [hiccup-convertable #_to-hiccup]]
   [reval.type.ui.simplevalue :refer [simplevalue->hiccup]]
   [reval.type.ui.list :refer [list->hiccup map->hiccup]]))

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
  hiccup-convertable
  (to-hiccup [self]
    ; warning because pr-str still might look good,
    ;(warnf "to-hiccuping unknown cljs type: %s data: %s " (type self) (pr-str self))
    [:div.bg-red-500 "unknown type: " (type self)
     (simplevalue->hiccup self "clj-unknown")]))

; nil values are a distinct thing of their own

(extend-type nil
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-nil")))

(extend-type cljs.core/Keyword
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-keyword")))

(extend-type cljs.core/Symbol
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-symbol")))

; would be cool to be able to use meta data to switch between
; if meta ^:br is set, then convert \n to [:br] otherwise to-hiccup the string as it is.
; however clojure does not support meta data for strings
(extend-type string
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-string")))

#_(extend-type char
    hiccup-convertable
    (to-hiccup [self]
      (simplevalue->hiccup self "clj-char")))

(extend-type number
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-long")))

(extend-type boolean
  hiccup-convertable
  (to-hiccup [self]
    (simplevalue->hiccup self "clj-boolean")))

;; LIST

(extend-type cljs.core/MapEntry
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-vector"
      :open "["
      :close  "]"
      :separator  ", "}
     self)))

(extend-type cljs.core/LazySeq
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-lazy-seq"
      :open  "("
      :close ")"
      :separator " "}
     self)))

(extend-type cljs.core/IntegerRange
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-int-range"
      :open  "("
      :close ")"
      :separator " "}
     self)))

(extend-type cljs.core/PersistentVector
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-vector"
      :open "["
      :close  "]"
      :separator " "}
     self)))

(extend-type cljs.core/List
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-list"
      :open "("
      :close  ")"
      :separator " "}
     self)))

(extend-type cljs.core/PersistentHashSet
  hiccup-convertable
  (to-hiccup [self]
    (list->hiccup
     {:class "clj-set"
      :open "#{"
      :close  "}"
      :separator  " "}
     self)))

;; MAPS

;; When we to-hiccup a map we will map over its entries, which will yield key-value pairs represented as vectors. To to-hiccup
;; the map we to-hiccup each of these key-value pairs with this helper function. They are to-hiccuped as list-likes with no
;; bracketing. These will then be assembled in to a list-like for the whole map by the IPersistentMap to-hiccup function.

(extend-type cljs.core/PersistentArrayMap
  hiccup-convertable
  (to-hiccup [self]
    (map->hiccup
     {:class "clj-map"
      :open "{"
      :close  "}"
      :separator  " "}
     self)))

;; This still needs to be implemented:
;; cljs.core/Range
;; cljs.core/Var
;; cljs.core/List
;; cljs.core/PersistentTreeMap


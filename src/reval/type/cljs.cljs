(ns picasso.render.cljs-types
  "equivalent to pinkgorilla.ui.renderer, but for clojurescript
   renders clojurescript data structure to html"
  (:require
   [taoensso.timbre :refer-macros [warnf errorf]]
   [picasso.protocols :refer [Renderable render]]
   [picasso.render.span :refer [span-render]]
   [picasso.render.list-like :refer [list-like-render list-like-render-map]]))

;;; ** Renderers for basic Clojure forms **

;; A default, catch-all renderer that takes anything we don't know what to do with and calls str on it.

;; https://grokbase.com/t/gg/clojure/121d2w4vhn/is-this-a-bug-extending-protocol-on-js-object
;; david nolan:
;; You should never extend js/Object.
;; It's unfortunate since this means we can't currently use js/Object to
;; provide default protocol implementations as we do in Clojure w/o fear of
;; conflicts with JavaScript libraries.

#_(extend-type js/Object
    Renderable
    (render [self]
      (span-render self "clj-unknown")))

(extend-type default
  Renderable
  (render [self]
    ; warning because pr-str still might look good,
    (warnf "rendering unknown cljs type: %s data: %s " (type self) (pr-str self))
    (span-render self "clj-unknown")))

; nil values are a distinct thing of their own

(extend-type nil
  Renderable
  (render [self]
    (span-render self "clj-nil")))

(extend-type cljs.core/Keyword
  Renderable
  (render [self]
    (span-render self "clj-keyword")))

(extend-type cljs.core/Symbol
  Renderable
  (render [self]
    (span-render self "clj-symbol")))

; would be cool to be able to use meta data to switch between
; if meta ^:br is set, then convert \n to [:br] otherwise render the string as it is.
; however clojure does not support meta data for strings
(extend-type string
  Renderable
  (render [self]
    (span-render self "clj-string")))

#_(extend-type char
    Renderable
    (render [self]
      (span-render self "clj-char")))

(extend-type number
  Renderable
  (render [self]
    (span-render self "clj-long")))

(extend-type boolean
  Renderable
  (render [self]
    (span-render self "clj-boolean")))

;; When we render a map we will map over its entries, which will yield key-value pairs represented as vectors. To render
;; the map we render each of these key-value pairs with this helper function. They are rendered as list-likes with no
;; bracketing. These will then be assembled in to a list-like for the whole map by the IPersistentMap render function.

(extend-type cljs.core/PersistentArrayMap
  Renderable
  (render [self]
    (list-like-render-map
     {:class "clj-map"
      :open "{"
      :close  "}"
      :separator  " "}
     self)))

(extend-type cljs.core/MapEntry
  Renderable
  (render [self]
    (list-like-render
     {:class "clj-vector"
      :open "["
      :close  "]"
      :separator  ", "}
     self)))

(extend-type cljs.core/LazySeq
  Renderable
  (render [self]
    (list-like-render
     {:class "clj-lazy-seq"
      :open  "("
      :close ")"
      :separator " "}
     self)))

(extend-type cljs.core/PersistentVector
  Renderable
  (render [self]
    (list-like-render
     {:class "clj-vector"
      :open "["
      :close  "]"
      :separator " "}
     self)))

(extend-type cljs.core/List
  Renderable
  (render [self]
    (list-like-render
     {:class "clj-list"
      :open "("
      :close  ")"
      :separator " "}
     self)))

(extend-type cljs.core/PersistentHashSet
  Renderable
  (render [self]
    (list-like-render
     {:class "clj-set"
      :open "#{"
      :close  "}"
      :separator  " "}
     self)))

;; This still needs to be implemented:
;; cljs.core/Range
;; cljs.core/Var
;; cljs.core/List
;; cljs.core/PersistentTreeMap


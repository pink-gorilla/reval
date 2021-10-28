(ns reval.type.converter
  (:require
   [reval.type.protocol :refer [to-hiccup]]))

(defn unknown-view [v]
  (let [type-as-str (-> v type str)]
    [:div.bg-red-300.border-solid.p-2
     [:p "unknown type"]
     [:h1 type-as-str]]))

(defn value-type->hiccup [v]
  (try
    (to-hiccup v)
    (catch Exception e
      (unknown-view v))))

(defn value->hiccup
  "converts a eval result to hiccup.
   this implementation is used as default in ns-eval
   can be used in nrepl nrepl middleware."
  [v]
  (let [m (meta v)]
    (cond
      ;(contains? m :r) (make :reagent {:hiccup v :map-keywords false})
      ;(contains? m :R) (make :reagent {:hiccup v :map-keywords true})
      ;(contains? m :p/render-as) (make :reagent {:hiccup v :map-keywords true})
      :else (value-type->hiccup v))))

(comment
  (value->hiccup 3)
;  
  )
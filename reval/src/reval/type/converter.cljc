(ns reval.type.converter
  (:require
   [reval.type.protocol :refer [to-hiccup]]))

(defn unknown-type-view [v]
  (let [type-as-str (-> v type str)]
    [:div.border-solid.p-2
     [:p.text-red-300 type-as-str]
     [:span (pr-str v)]]))

(def nil-view
  [:div.p-2.clj-nil
   [:p "nil"]])

#?(:clj
   (defn value-type->hiccup [v]
     (try
       (to-hiccup v)
       (catch Exception _
         (unknown-type-view v))))

   :cljs
   (defn value-type->hiccup [v]
     (try
       (to-hiccup v)
       (catch :default e ; js/Exception _
         (unknown-type-view v)))))

(defn value->hiccup
  "converts a eval result to hiccup.
   this implementation is used as default in ns-eval
   can be used in nrepl nrepl middleware."
  [v]
  (if v
    (let [m (meta v)]
      (cond
        (contains? m :R) v
        (contains? m :fh) v ; another name for :R
        (contains? m :hidden) [:div.no-hiccup]
        (contains? m :render-as) [(:render-as m) v]
        :else (value-type->hiccup v)))
    nil-view))

(comment
  (value->hiccup 3)
;  
  )
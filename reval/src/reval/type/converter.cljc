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
       (with-meta (to-hiccup v) {:hiccup true})
       (catch Exception _
         (unknown-type-view v))))

   :cljs
   (defn value-type->hiccup [v]
     (try
       (to-hiccup v)
       (catch js/Exception _
         (unknown-type-view v)))))


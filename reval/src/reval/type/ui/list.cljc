(ns reval.type.ui.list
  (:require
   [reval.type.protocol :refer [hiccup-convertable to-hiccup]]))

(defn box [class open close inside]
  [:span {:class class}
   [:span.font-bold.teal-700.mr-1 open]
   (into [:span.items] inside)
   [:span.font-bold.teal-700.ml-1 close]])

(defn list->hiccup
  [{:keys [class open close separator]} list]
  (box class open close
       (->> (map to-hiccup list)
            (interpose [:span separator]))))

(defn map->hiccup [options entry]
  (list->hiccup options (interleave
                         (keys entry)
                         (vals entry))))

(comment
  (list->hiccup
   {:class "clj-lazy-seq"
    :open "("
    :close ")"
    :separator " "}
   [1 "test" 5.3 nil :super])

;  
  )

(ns reval.dali.viewer.list
  (:require 
    [dali.viewer :refer [viewer2]]))

;; used to show seq/vec/maps where the children 
;; are dali-specs

(defn one [dali-spec]
  [:span.mr-1
    [viewer2 dali-spec]])

(defn list-view [{:keys [class open close children]} ]
    [:span {:class class}
     [:span.font-bold.teal-700.mr-1 open]
     ; [:span "#" (count children)]
     (into [:span.items] 
           (map one children))
     [:span.font-bold.teal-700.ml-1 close]])



(ns reval.dali.viewer.list
  (:require
   [dali.viewer :refer [viewer2]]))

;; used to show seq/vec/maps where the children 
;; are dali-specs

(def ^:private delim-style
  {:font-weight "700"
   :color "#0f766e"})

(defn one [dali-spec]
  [:span {:style {:margin-right "4px"}}
   [viewer2 dali-spec]])

(defn list-view [{:keys [class open close children]}]
  [:span {:class class}
   [:span {:style (assoc delim-style :margin-right "4px")} open]
   (into [:span.items]
         (map one children))
   [:span {:style (assoc delim-style :margin-left "4px")} close]])



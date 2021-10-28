(ns reval.type.hiccup)

(defprotocol hiccup-convertable
  (to-hiccup [self]))

(defmulti paint :type)

(defmethod paint :default [{:keys [type] :as picasso-spec}]
  [:div.bg-red-300.border-solid
   [:h1 (str "Unknown type: [" type "] ")]])

(defn span-render
  [thing class]
  [:span {:class class} (pr-str thing)])


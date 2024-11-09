(ns reval.dali.viewer.evalerr)


;; currently not used.

(def show-stacktrace true)

(defn stacktrace-line [idx {:keys [_name file line _class method
                                   ;type   flags ns fn
                                   ]}]
  (let [;tooling? (contains? flags :tooling)
        row-classes "" #_(str (name type) (when tooling? " tooling-stackframe"))]
    ^{:key idx}
    [:tr {:class row-classes}
     (case type
       :clj [:<>
             [:td [:span.text-blue-900 "ns"]]
             [:td [:span.text-blue-900 "fn"]]
             [:td [:span.text-blue-900 (str file ": " line)]]]
       :java [:<>
              [:td]
              [:td [:span.text-green-300 method]]
              [:td [:span.text-green-300 (str file ": " line)]]]
       [:<>
        [:td  [:span.text-red-300 method]]
        [:td [:span.text-red-300 method]]
        [:td [:span.text-red-300 (str file ": " line)]]])]))

(defn evalerr [{:keys [message class stacktrace] :as err}]
  [:div.text-red-500
   [:p class]
   [:p message]
   (when (and show-stacktrace stacktrace)
     [:table.w-full.text-md.bg-white.shadow-md.rounded.mb-4
      [:tbody
       (map-indexed stacktrace-line stacktrace)]])])
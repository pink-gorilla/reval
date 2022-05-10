(def show-stacktrace true)
(def show-segment-debug-ui false) ; true for debugging
;; ervalerr

(defn stacktrace-line [idx {:keys [name file line class method
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

(pinkie/register-tag :p/evalerr evalerr)

;; segment

(defn segment-debug [segment]
  [:div.bg-gray-500
   [:p.font-bold "segment debug ui"]
   (pr-str segment)])

(defn segment [{:keys [src err out hiccup] :as segment}]
  (let [scode (:code segment)]
    [:div.flex.flex-col
     (when scode
       [user/highlightjs scode])
     (when err
       [evalerr err])
     (when (not (string/blank? out))
       [:div.bg-blue-200
        [text2 out]])
     (when hiccup
       [:div.mt-1.mb-1
        (render-vizspec2 hiccup)])
     (when show-segment-debug-ui
       [segment-debug segment])]))
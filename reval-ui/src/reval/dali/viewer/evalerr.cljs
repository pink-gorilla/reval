(ns reval.dali.viewer.evalerr)

;; currently not used.

(def show-stacktrace true)

(defn stacktrace-line [idx {:keys [_name file line _class method type
                                   ; flags ns fn
                                   ]}]
  (let [;tooling? (contains? flags :tooling)
        row-classes "" #_(str (name type) (when tooling? " tooling-stackframe"))]
    ^{:key idx}
    [:tr {:class row-classes}
     (case type
       :clj [:<>
             [:td [:span {:style {:color "#1e3a8a"}} "ns"]]
             [:td [:span {:style {:color "#1e3a8a"}} "fn"]]
             [:td [:span {:style {:color "#1e3a8a"}} (str file ": " line)]]]
       :java [:<>
              [:td]
              [:td [:span {:style {:color "#86efac"}} method]]
              [:td [:span {:style {:color "#86efac"}} (str file ": " line)]]]
       [:<>
        [:td [:span {:style {:color "#fca5a5"}} method]]
        [:td [:span {:style {:color "#fca5a5"}} method]]
        [:td [:span {:style {:color "#fca5a5"}} (str file ": " line)]]])]))

(defn evalerr [{:keys [message class stacktrace] :as err}]
  [:div {:style {:color "#ef4444"}}
   [:p class]
   [:p message]
   (when (and show-stacktrace stacktrace)
     [:table {:style {:width "100%"
                      :font-size "1rem"
                      :background "#fff"
                      :box-shadow "0 4px 6px -1px rgba(0,0,0,0.1)"
                      :border-radius "4px"
                      :margin-bottom "1rem"}}
      [:tbody
       (map-indexed stacktrace-line stacktrace)]])])
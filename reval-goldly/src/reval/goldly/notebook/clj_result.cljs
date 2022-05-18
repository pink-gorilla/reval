(ns reval.goldly.notebook.clj-result
  (:require
   [rf]
   [string]
   [reval.goldly.ui-helper :refer [text2]]
   [reval.goldly.vizspec :refer [render-vizspec2]]))

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

;; notebook

(def show-notebook-debug-ui false)

(defn notebook-debug [nb]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "notebook debug ui"]
   (pr-str nb)])

(defn notebook [{:keys [meta content] :as nb}]
  (let [{:keys [ns eval-time]} meta]
    [:div.bg-indigo-50.p-2
     [:h1.text-xl.text-blue-800.text-xl.pb-2 ns]
     [:p.pb-2 "evaluated: " eval-time]
     [:hr.h-full.border-solid]
     (into [:div]
           (map segment content))
     (when show-notebook-debug-ui
       [notebook-debug nb])]))

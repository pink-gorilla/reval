(ns reval.dali.viewer.notebook
  (:require
   [clojure.string :refer [blank?]]
   [ui.highlightjs :refer [highlightjs]]
   [dali.viewer :refer [viewer2]]
   [reval.helper.ui-helper :refer [text2]]))

;; segment

;{:id nil,
; :code "(def a 34)",
; :out "",
; :ns "reval.goldly.page.repl",
; :render-fn reval.goldly.viz.render-fn/reagent,
; :data [:span {:style {:color "steelblue"}}
;        "#'reval.goldly.page.repl/a"]}

(defn segment [{:keys [_id _ns _src err out result] :as segment}]
  (let [scode (:code segment)]
    [:div.flex.flex-col
     ;(pr-str segment)
     (when scode
       [highlightjs scode])
     (when result
       [:div.mt-1.mb-1.w-full.h-full
        {:style {:max-width "800px"
                 :max-height "400px"}}
        [viewer2 result]])
     (when err
       [:div.mt-1.mb-1.w-full.h-full
        {:style {:max-width "800px"
                 :max-height "400px"}}
        [viewer2 err]])
     (when (not (blank? out))
       [:div.bg-blue-200.max-w-full.overflow-x-auto
        [text2 out]])]))

;; notebook

(def show-notebook-debug-ui false)

(defn notebook-debug [nb]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "notebook debug ui"]
   (pr-str nb)])

(defn notebook [{:keys [meta content] :as nb}]
  (let [{:keys [ns eval-time]} meta]
    [:div.bg-indigo-50.p-2.max-w-full.overflow-x-auto
     [:h1.text-xl.text-blue-800.text-xl.pb-2 ns]
     [:p.pb-2 "evaluated: " eval-time]
     [:hr.h-full.border-solid]
     (into [:div]
           (map segment content))
     (when show-notebook-debug-ui
       [notebook-debug nb])]))

(defn empty-notebook []
  {:meta {}
   :content []})

(defn add-segment [notebook segment]
  (update notebook :content concat [segment]))

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
    [:div {:style {:display "flex"
                   :flex-direction "column"}}
     (when scode
       [highlightjs scode])
     (when result
       [:div {:style {:margin-top "4px"
                      :margin-bottom "4px"
                      :width "100%"
                      :height "100%"
                      :max-width "800px"
                      :max-height "400px"}}
        [viewer2 result]])
     (when err
       [:div {:style {:margin-top "4px"
                      :margin-bottom "4px"
                      :width "100%"
                      :height "100%"
                      :max-width "800px"
                      :max-height "400px"}}
        [viewer2 err]])
     (when (not (blank? out))
       [:div {:style {:background "#bfdbfe"
                      :max-width "100%"
                      :overflow-x "auto"}}
        [text2 out]])]))

;; notebook

(def show-notebook-debug-ui false)

(defn notebook-debug [nb]
  [:div {:style {:background "#6b7280"
                 :margin-top "1.25rem"}}
   [:p {:style {:font-weight "700"}} "notebook debug ui"]
   (pr-str nb)])

(defn notebook [{:keys [meta content] :as nb}]
  (let [{:keys [ns eval-time]} meta]
    [:div {:style {:background "#eef2ff"
                   :padding "8px"
                   :max-width "100%"
                   :overflow-x "auto"}}
     [:h1 {:style {:font-size "1.25rem"
                   :line-height "1.75rem"
                   :color "#1e40af"
                   :padding-bottom "8px"
                   :margin "0"}}
      ns]
     [:p {:style {:padding-bottom "8px"
                  :margin "0"}}
      "evaluated: " eval-time]
     [:hr {:style {:width "100%"
                   :border-style "solid"}}]
     (into [:div]
           (map segment content))
     (when show-notebook-debug-ui
       [notebook-debug nb])]))

(defn empty-notebook []
  {:meta {}
   :content []})

(defn add-segment [notebook segment]
  (update notebook :content concat [segment]))

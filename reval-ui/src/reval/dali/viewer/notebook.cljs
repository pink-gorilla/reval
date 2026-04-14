(ns reval.dali.viewer.notebook
  (:require
   [clojure.string :refer [blank?]]
   [ui.highlightjs :refer [highlightjs]]
   [dali.viewer :refer [viewer2]]
   [reval.helper.ui-helper :refer [text2]]))

;; segment

(defn segment [{:keys [_id _ns err out result code] :as _segment}]
  [:div.reval-notebook-segment
   (when code
     [:div.reval-notebook-segment-code
      [highlightjs code]])
   (when result
     [:div.reval-notebook-segment-result
      [viewer2 result]])
   (when err
     [:div.reval-notebook-segment-error
      [viewer2 err]])
   (when (not (blank? out))
     [:div.reval-notebook-segment-out
      [text2 out]])])

;; notebook

(defn notebook [{:keys [meta content] :as nb}]
  (let [{:keys [ns eval-time]} meta]
    [:div.reval-notebook
     [:div.reval-notebook-header
      [:div.reval-notebook-header-row
       [:h1.reval-notebook-header-ns ns]
       [:p.reval-notebook-header-eval-time "evaluated: " eval-time]]
      [:hr.reval-notebook-header-hr]]

     (into [:div.reval-notebook-segments]
           (map segment content))]))

(defn empty-notebook []
  {:meta {}
   :content []})

(defn add-segment [notebook segment]
  (update notebook :content concat [segment]))

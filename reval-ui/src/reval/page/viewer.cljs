(ns reval.page.viewer
  (:require
   [reitit.frontend.easy :as rfe]
   [spaces.core]
   [reval.dali.viewer.collection-viewer :refer [collection-viewer]]
   [reval.dali.viewer.notebook-viewer :refer [notebook-viewer]]))

;; NOTEBOOK UI

;; APP

;(if (< 500 (.-availWidth js/screen)) ; big screen

(defn goto-nb [{:keys [nbns]}]
  (rfe/navigate 'reval.page.viewer/viewer-page {:query-params {:nbns nbns}}))

(defn viewer [_query-params]
  (fn [{:keys [nbns fmt]
        :or {fmt :clj}}]
    [spaces.core/viewport
     [spaces.core/left-resizeable {:size "20%"
                                   :style {:background "#f3f4f6"
                                           :max-height "100%"
                                           :overflow-y "auto"}}
      [collection-viewer
       {:link goto-nb}]]
     [spaces.core/fill {:style {:background "#f3f4f6"
                                :max-height "100%"
                                :overflow-y "auto"}}
      [notebook-viewer {:nbns nbns}]]]))

(defn viewer-page [{:keys [_route-params query-params _handler] :as _route}]
  [:div {:style {:background "#86efac"
                 :position "fixed"
                 :width "100vw"
                 :height "100vh"
                 :left "0px"
                 :top "0px"
                 :margin "0px"
                 :padding "0px"
                 }}
   [viewer query-params]])



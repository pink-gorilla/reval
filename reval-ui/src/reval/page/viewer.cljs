(ns reval.page.viewer
  (:require
   [re-frame.core :as rf]
   [spaces.core]
   [reval.dali.viewer.collection-viewer :refer [collection-viewer]]
   [reval.dali.viewer.notebook-viewer :refer [notebook-viewer]]))

;; NOTEBOOK UI

;; APP

;(if (< 500 (.-availWidth js/screen)) ; big screen

(defn goto-nb [{:keys [nbns]}]
  (rf/dispatch [:bidi/goto 'reval.page.viewer/viewer-page
                :query-params {:nbns nbns}]))

(defn viewer [_query-params]
  (fn [{:keys [nbns fmt]
        :or {fmt :clj}}]
    [spaces.core/viewport
     [spaces.core/left-resizeable {:size "20%"
                                   :class "bg-gray-100 max-h-full overflow-y-auto"}
      [collection-viewer
       {:link goto-nb}]]
     [spaces.core/fill {:class "bg-gray-100 max-h-full overflow-y-auto"}
      [notebook-viewer {:nbns nbns}]]]))

(defn viewer-page [{:keys [_route-params query-params _handler] :as _route}]
  [:div.bg-green-300.w-screen.h-screen
   [viewer query-params]])



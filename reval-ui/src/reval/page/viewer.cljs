(ns reval.page.viewer
  (:require
   [spaces.core]
   [reval.dali.viewer.collection-viewer :refer [collection-viewer]]
   [reval.dali.viewer.notebook-viewer :refer [notebook-viewer]]))

;; NOTEBOOK UI

;; APP

;(if (< 500 (.-availWidth js/screen)) ; big screen


(defn viewer [_query-params]
  (fn [{:keys [ns fmt]
        :or {fmt :clj}}]
    (let [fmt (if (string? fmt)
                (keyword fmt)
                fmt)]
      [spaces.core/viewport
       [spaces.core/left-resizeable {:size "20%"
                                     :class "bg-gray-100 max-h-full overflow-y-auto"}
        [collection-viewer
         {:link 'reval.page.viewer/viewer-page}]]
       [spaces.core/fill {:class "bg-gray-100 max-h-full overflow-y-auto"}
        [notebook-viewer {:nbns ns}]
        ;[:div "nb viewer"]
        ]])))

(defn viewer-page [{:keys [_route-params query-params _handler] :as _route}]
  [:div.bg-green-300.w-screen.h-screen
   [viewer query-params]])



(ns reval.page.viewer
  (:require
   [spaces.core]
   [reval.helper.url-loader :refer [url-loader]]
   [reval.notebook-ui.collection :refer [notebook-collection]]
   [reval.notebook-ui.clj-result :refer [notebook]]))

;; NOTEBOOK UI

(def show-viewer-debug-ui false) ; true for debugging

;; APP

(def nb-welcome
  {:meta {:ns "goldly.welcome"}
   :content
   [{:code "(println \"Welcome to Goldly Notebook Viewer \")"
     :hiccup [:h1.text-blue-800 "Welcome to Notebook Viewer!"]
     :out "Welcome to Goldly Notebook Viewer"}]})

(defn viewer-debug [query-params]
  [:div.bg-gray-500.pt-10.hoover-bg-blue-300
   [:p.font-bold "viewer debug ui"]
   [:p "query params:"]
   [:p (pr-str query-params)]
   [:p "ns: " (:ns query-params)]])

;(if (< 500 (.-availWidth js/screen)) ; big screen
;(when show-viewer-debug-ui
;  [viewer-debug query-params])

(defn viewer [_query-params]
  (fn [{:keys [ns fmt]
        :or {fmt :clj}}]
    (let [fmt (if (string? fmt)
                (keyword fmt)
                fmt)]
      [spaces.core/viewport
       [spaces.core/left-resizeable {:size "20%"
                                :class "bg-gray-100 max-h-full overflow-y-auto"}
        [url-loader {:fmt :clj
                     :url 'reval.document.collection/nb-collections}
         #(notebook-collection 'reval.page.viewer/viewer-page %)]]
       [spaces.core/fill {:class "bg-gray-100 max-h-full overflow-y-auto"}
        [url-loader {:fmt :clj
                     :url 'reval.document.notebook/load-notebook
                     :args-fetch [ns fmt]}
         notebook]]])))

(defn viewer-page [{:keys [_route-params query-params _handler] :as _route}]
  [:div.bg-green-300.w-screen.h-screen
   [viewer query-params]])



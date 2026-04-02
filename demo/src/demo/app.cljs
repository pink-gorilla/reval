(ns demo.app
  (:require
   [frontend.css :refer [css-loader]]
   [shadowx.core :refer [get-resource-path]]))

(defn wrap [page match]
  [:div
   [css-loader (get-resource-path)]
   [page match]])

(def routes
  [["/" {:name 'reval.page.viewer/viewer-page}]
   ["/frepl" {:name 'demo.page.frepl/page}]
   ["/repl" {:name 'reval.page.repl/repl-page}]
   ["/directory-explorer" {:name 'demo.page.directory-explorer/page}]])


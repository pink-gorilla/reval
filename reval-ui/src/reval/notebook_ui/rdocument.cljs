(ns reval.notebook-ui.rdocument
  (:require
   [reval.helper.ui-helper :refer [text2]]
   [reval.helper.url-loader :refer [url-loader]]))

;; render functions 

(defn img [url _args]
  [:img.p-4 {:src url}])

(defn text-data [data args]
  [text2 (or (first args) {}) data])

(defn text-url [url args]
  [:div.p-4
   ;test if text2 works
   ;[text2 {:class "bg-blue-300 text-red-500"} "asdf\nasdf\n"]
   [url-loader {:url url
                :args args}
    text-data]])

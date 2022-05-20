(ns reval.goldly.notebook.rdocument
  (:require
   [reval.goldly.ui-helper]
   [reval.goldly.url-loader]))

;; render functions 

(defn img [url args]
  [:img.p-4 {:src url}])

(defn text-data [data args]
  [reval.goldly.ui-helper/text2 (or (first args) {}) data])

(defn text-url [url args]
  [:div.p-4
   ;test if text2 works
   ;[text2 {:class "bg-blue-300 text-red-500"} "asdf\nasdf\n"]
   [reval.goldly.url-loader/url-loader {:url url
                :args args}
    text-data]])
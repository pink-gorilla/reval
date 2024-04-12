(ns reval.helper.ui-helper
  (:require
   [re-frame.core :as rf]
   [clojure.string :refer [split]]))

(defn link-fn [fun text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn line-with-br [t]
  [:div
   [:span.font-mono.whitespace-pre t]
   [:br]])

(defn text2
  "Render text (as string) to html
   works with \\n (newlines)
   Needed because \\n is meaningless in html"
  ([t]
   (text2 {} t))
  ([opts t]
   (let [lines (split t #"\n")]
     (into
      [:div (merge {:class "textbox text-lg"} opts)]
      (map line-with-br lines)))))



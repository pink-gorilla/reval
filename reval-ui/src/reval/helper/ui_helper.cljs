(ns reval.helper.ui-helper
  (:require
   [re-frame.core :as rf]
   [reagent.core :as r]
   [clojure.string :refer [split]]))

(defn link-fn [fun text]
  (let [hover? (r/atom false)]
    (fn [fun text]
      [:a {:style {:background (if @hover? "#b91c1c" "#2563eb")
                   :color "#fff"
                   :cursor "pointer"
                   :margin "4px"
                   :padding "2px 6px"
                   :border-radius "2px"
                   :text-decoration "none"
                   :display "inline-block"}
           :on-mouse-enter #(reset! hover? true)
           :on-mouse-leave #(reset! hover? false)
           :on-click fun}
       text])))

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn line-with-br [t]
  [:div
   [:span {:style {:font-family "monospace"
                   :white-space "pre"}}
    t]
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
      [:div (merge {:class "textbox"
                    :style {:font-size "1.125rem"
                            :line-height "1.75rem"}}
                   opts)]
      (map line-with-br lines)))))



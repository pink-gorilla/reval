(ns reval.kernel.clj-eval-hiccup-test
  (:require
   [clojure.test :refer [deftest are]]
   [reval.kernel.clj-eval :refer [clj-eval]]
   [reval.document.notebook :refer [eval-result->hiccup]]
   [reval.test-init]))

(defn eval-src->hiccup [src]
  (-> {:code src}
      clj-eval
      eval-result->hiccup
      :hiccup))

(deftest test-eval-clj
  (are [code hiccup]
       (= hiccup (eval-src->hiccup code))

    "13"
    [:span {:style {:color "blue"}} "13"]

    "[7 8]"
    [:span {:class "clj-vector"} 
     [:span.font-bold.teal-700.mr-1 "["] 
     [:span.items 
      [:span {:style {:color "blue"}} "7"] 
      [:span " "] 
      [:span {:style {:color "blue"}} "8"]] 
     [:span.font-bold.teal-700.ml-1 "]"]]

    ))

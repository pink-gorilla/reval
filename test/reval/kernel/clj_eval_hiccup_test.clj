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
    [:span {:class "clj-long"} "13"]

    "[7 8]"
    [:div.bg-red-300.border-solid.p-2 [:p "unknown type"] [:h1 "class clojure.lang.PersistentVector"]]

    #_{:picasso
       {:type :list-like
        :content
        {:class "clj-vector"
         :open "["
         :close "]"
         :separator " "
         :items
         '({:type :hiccup, :content [:span {:class "clj-long"} "7"]}
           {:type :hiccup, :content [:span {:class "clj-long"} "8"]})
         :value "[7 8]"}}}))

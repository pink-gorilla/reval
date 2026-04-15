(ns reval.kernel.clj-eval-hiccup-test
  (:require
   [clojure.test :refer [deftest are]]
   [reval.kernel.clj-eval :refer [clj-eval]]
   [reval.kernel.dali :refer [dalify]]
   [reval.test-init]))

(defn eval-src->hiccup [src]
  (-> {:code src}
      clj-eval
      dalify
      :result
      :data
      ))

(def vec-result
 )

(deftest test-eval-clj
  (are [code hiccup]
       (= hiccup (eval-src->hiccup code))

    "13"
    [:span {:class "clj-long"} "13"]

    ;"[7 8]"
    #_{:viewer-fn 'reval.dali.viewer.list/list-view,
       :transform-fn nil,
       :data
       {:class "clj-vector",
        :open "[",
        :close "]",
        :separator " ",
        :children
        '({:viewer-fn 'dali.viewer.hiccup/hiccup,
           :transform-fn nil,
           :data [:span {:style {:color "blue"}} "7"],
           :children nil,
           :store-format nil,
           :store-data nil,
           :store-set-url nil}
          {:viewer-fn 'dali.viewer.hiccup/hiccup,
           :transform-fn nil,
           :data [:span {:style {:color "blue"}} "8"],
           :children nil,
           :store-format nil,
           :store-data nil,
           :store-set-url nil})},
       :children nil,
       :store-format nil,
       :store-data nil,
       :store-set-url nil}
    #_[:span {:class "clj-vector"}
       [:span.font-bold.teal-700.mr-1 "["]
       [:span.items
        [:span {:style {:color "blue"}} "7"]
        [:span " "]
        [:span {:style {:color "blue"}} "8"]]
       [:span.font-bold.teal-700.ml-1 "]"]]))

(comment

  (eval-src->hiccup "13")
  (eval-src->hiccup "[7 8]")
  (= [:span {:class "clj-vector"} [:span.font-bold.teal-700.mr-1 "["] [:span.items [:span {:style {:color "blue"}} "7"] [:span " "] [:span {:style {:color "blue"}} "8"]] [:span.font-bold.teal-700.ml-1 "]"]] (eval-src->hiccup "[7 8]")))
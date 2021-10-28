(ns reval.type.unknown-test
  (:require
   [clojure.test :refer :all]
   [reval.type.protocol :refer [to-hiccup]]
   [reval.type.converter :refer [value->hiccup unknown-view]]))

(defrecord unknown-type-record [a])

(deftest unknown-test
  (let [r (unknown-type-record. 3)
        hiccup  (-> r value->hiccup)
        hiccup-unknown (unknown-view r)]
    (is (= hiccup hiccup-unknown))))

(comment
  (-> (unknown-type-record. 3)
      value->hiccup)
;  
  )






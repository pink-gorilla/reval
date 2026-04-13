(ns dali.type.unknown-test
  (:require
   [clojure.test :refer [deftest is]]
   [dali.type.protocol :refer [to-hiccup]]
   [dali.type.converter :refer [unknown-type-view]]
   [reval.test-init]))

(defrecord unknown-type-record [a])

; records are rendered with clj-record. 
; need to search for something better.

#_(deftest unknown-test
    (let [r (unknown-type-record. 3)
          hiccup  (-> r value->hiccup)
          hiccup-unknown (unknown-type-view r)]
      (is (= hiccup hiccup-unknown))))

(comment
  (-> (unknown-type-record. 3)
      value->hiccup)
;  
  )






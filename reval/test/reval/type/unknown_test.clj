(ns reval.type.unknown-test
  (:require
   [clojure.test :refer [deftest is]]
   [dali.type.protocol :refer [to-dali]]
   [dali.type.error :refer [unknown-type]]
   [reval.test-init]))

(defrecord unknown-type-record [a])

; records are rendered with clj-record. 
; need to search for something better.

#_(deftest unknown-test
    (let [r (unknown-type-record. 3)
          hiccup  (-> r value->hiccup)
          hiccup-unknown (unknown-type r)]
      (is (= hiccup hiccup-unknown))))

(comment
  (-> (unknown-type-record. 3)
      value->hiccup)
;  
  )






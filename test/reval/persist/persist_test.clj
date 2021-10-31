(ns reval.persist.persist-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.persist.protocol :refer [loadr save]]
   [reval.test-init]))

(deftest json-reload-test
  (let [data {:a 1 :b 2 :c [1 2 3]}]
    (save :json "jsonreload.json" data)
    (is (= data (loadr :json "jsonreload.json")))))

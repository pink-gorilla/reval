(ns reval.persist.persist-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.persist.protocol :refer [loadr save]]
   [reval.persist.edn :refer [read-str pprint-str]]
   [reval.test-init]))

(deftest json-reload-test
  (let [data {:a 1 :b 2 :c [1 2 3]}]
    (save :json "/tmp/jsonreload.json" data)
    (is (= data (loadr :json "/tmp/jsonreload.json")))))

(deftest localdate-reload-test
  (let [sdate (str "#time/date \"2011-01-01\"" "\n")
        stime (str "#time/date-time \"2021-11-04T00:52:59.694154533\"" "\n")]
    (is (= sdate (-> sdate read-str pprint-str)))
    (is (= stime (-> stime read-str pprint-str)))))







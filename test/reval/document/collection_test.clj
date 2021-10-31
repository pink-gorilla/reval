(ns reval.document.collection-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.persist.protocol :refer [loadr]]
   [reval.document.collection :refer [get-collections]]
   [reval.test-init]))

(deftest collection-overview-test
  (is (= (get-collections
          {:demo [:clj "demo/notebook/"]
           :user [:clj "test/notebook/"]})
         {:demo '("demo.notebook.image")
          :user '("test.notebook.apple" "test.notebook.banana")})))


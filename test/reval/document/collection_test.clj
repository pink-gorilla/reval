(ns reval.document.collection-test
  (:require
   [clojure.test :refer :all]
   [reval.persist.protocol :refer [loadr]]
   [reval.document.collection :refer [get-ns-overview]]
   [reval.test-init]))

(deftest collection-overview-test
  (is (= (get-ns-overview)
         {:demo '("demo.notebook.image")
          :user '("demo.notebook_test.apple" "demo.notebook_test.banana")})))
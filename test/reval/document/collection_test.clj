(ns reval.document.collection-test
  (:require
   [clojure.test :refer [deftest is]]
   [modular.persist.protocol :refer [loadr]]
   [reval.document.collection :refer [get-collections]]
   [reval.test-init]))

(deftest collection-overview-test
  (is (= (get-collections
          {:demo [:clj "demo/notebook/"]
           :user [:clj "test/notebook/"]})
         {:demo [:clj ["demo.notebook.reval-image"]]
          :user [:clj ["test.notebook.apple"]]})))

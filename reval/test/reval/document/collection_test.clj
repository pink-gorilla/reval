(ns reval.document.collection-test
  (:require
   [clojure.test :refer [deftest is]]
   [modular.persist.protocol :refer [loadr]]
   [reval.document.collection :refer [get-collections]]
   [reval.test-init]))

(deftest collection-overview-test
  (is (= (get-collections
          {:demo [:clj "demo/notebook/"]})
         {:demo [:clj []]}))

  ;{:user [:clj [{:nbns "test.notebook.apple",
  ;               :ext "clj",
  ;                     ;:path "/home/florian/repo/gorilla/reval/reval/test/test/notebook/apple.clj"
  ;               }]]}
  (is (= (-> (get-collections {:user [:clj "test/notebook/"]})
             :user
             second
             first
             (dissoc :path))
         {:nbns "test.notebook.apple"
          :ext "clj"
         ;:path "/home/florian/repo/gorilla/reval/reval/test/test/notebook/apple.clj"
          })))
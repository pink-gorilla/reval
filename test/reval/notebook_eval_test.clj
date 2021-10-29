(ns reval.notebook-eval-test
  (:require
   [clojure.test :refer :all]
   [reval.persist.edn :refer [loadr]]
   [reval.ns-eval :refer [eval-ns]]))

; this test relies on the default storage path
; defined in reval.config.

(deftest notebook-eval-test
  (eval-ns "notebook.apple")
  (let [nb (loadr "/tmp/document/notebook.apple/notebook.edn")]
    (is (= (:ns nb) "notebook.apple"))))

(comment
  (eval-ns "notebook.apple")
  (loadr "document/notebook.apple/notebook.edn")

;  
  )




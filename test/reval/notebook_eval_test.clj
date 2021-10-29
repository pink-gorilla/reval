(ns reval.notebook-eval-test
  (:require
   [clojure.test :refer :all]
   [reval.persist.protocol :refer [loadr]]
   [reval.ns-eval :refer [eval-ns]]
   [reval.default] ;; side effects
   [reval.config :refer [use-tmp]]))

; this test relies on the default storage path
; defined in reval.config.

(use-tmp)

(deftest notebook-eval-test
  (eval-ns "demo.notebook.apple")
  (let [nb (loadr :edn "/tmp/document/demo.notebook.apple/notebook.edn")]
    (is (= (:ns nb) "demo.notebook.apple"))))

(comment
  (eval-ns "demo.notebook.apple")
  (loadr "document/demonotebook.apple/notebook.edn")

;  
  )




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
  (let [nb (loadr :edn "/tmp/rdocument/demo.notebook.apple/notebook.edn")
        segments (:content nb)]
    (is (= (:ns nb) "demo.notebook.apple"))
    (is (= (count segments) 5))
    (is (= (-> (get segments 0) :hiccup)
           ; (ns ) evaluates to nil.
           [:div.p-2.clj-nil [:p "nil"]]))
    (is (= (-> (get segments 1) :hiccup)
           ; (+ 1 1) evaluates to 2
           [:span {:class "clj-long"} "2"]))
    (is (= (-> (get segments 3) :out)
           ; "(println \"hello\")" gives :out hello
           "hello\n"))))

(comment
  (eval-ns "demo.notebook.apple")
  (loadr "document/demonotebook.apple/notebook.edn")

;  
  )

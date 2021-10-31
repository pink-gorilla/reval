(ns reval.document.notebook-eval-test
  (:require
   [clojure.test :refer :all]
   [reval.persist.protocol :refer [loadr]]
   [reval.document.notebook :refer [eval-notebook]]
   [reval.test-init]))

; this test relies on the default storage path
; defined in reval.config.

(deftest notebook-eval-test
  (eval-notebook "demo.notebook-test.apple")
  (let [nb (loadr :edn "/tmp/rdocument/demo/notebook_test/apple/notebook.edn")
        segments (:content nb)]
    (is (= (get-in nb [:meta :ns]) "demo.notebook-test.apple"))
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
  (eval-notebook "demo.notebook-test.apple")
  (loadr :edn "demo/rdocument/demo/notebook_test/apple/notebook.edn")

;  
  )

(ns reval.document.notebook-eval-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.document.notebook :refer [eval-notebook load-notebook]]
   [reval.test-init]))

; this test relies on the default storage path
; defined in reval.config.

(deftest notebook-eval-test
  (eval-notebook "test.notebook.apple")
  (let [nb  (load-notebook "test.notebook.apple")
        ;;(loadr :edn "/tmp/rdocument/test/notebook/apple/notebook.edn")
        segments (:content nb)]
    (is (= (get-in nb [:meta :ns]) "test.notebook.apple"))
    (is (= (count segments) 6))
    (is (= (-> (get segments 0) :hiccup)
           ; (ns ) evaluates to nil.
           [:div.p-2.clj-nil [:p "nil"]]))
    (is (= (-> (get segments 1) :hiccup)
           ; (+ 1 1) evaluates to 2
           [:span {:style {:color "blue"}} "2"]))
    (is (= (-> (get segments 3) :out)
           ; "(println \"hello\")" gives :out hello
           "hello\n"))))

(comment
  (eval-notebook "test.notebook.apple")
  (load-notebook "test.notebook.apple")
  ;(loadr :edn "demo/rdocument/demo/notebook_test/apple/notebook.edn")

;  
  )

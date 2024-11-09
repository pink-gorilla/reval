(ns reval.document.notebook-eval-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.document.manager :refer [fpath url-root get-link-ns get-path-ns]]
   [reval.document.notebook :refer [eval-notebook load-notebook]]
   [reval.test-init]))

(def this {:config {:rdocument  {:fpath "/tmp/rdocument"
                                 :rpath "/api/rdocument/file"}
                    :collections {:user [:clj "user/notebook/"]
                                  :demo [:clj "demo/notebook/"]
                                  :demo-cljs [:cljs "demo/notebook/"]}}})

(deftest config-test
  (let [rpath-val (fpath this)
        url-root-val (url-root this)
        path-ns (get-path-ns this 'demo.apple)]
    (is (= rpath-val "/tmp/rdocument"))
    (is (= url-root-val "/api/rdocument/file"))
    (is (= path-ns "/tmp/rdocument/demo/apple"))))

(deftest notebook-eval-test
  (eval-notebook this "test.notebook.apple")
  (let [nb  (load-notebook this "test.notebook.apple")
        ;;(loadr :edn "/tmp/rdocument/test/notebook/apple/notebook.edn")
        segments (:content nb)]
    (is (= (get-in nb [:meta :ns]) "test.notebook.apple"))
    (is (= (count segments) 6))
    (is (= (-> (get segments 0) :data)
           ; (ns ) evaluates to nil.
           [:div.p-2.clj-nil [:p "nil"]]))
    (is (= (-> (get segments 1) :data)
           ; (+ 1 1) evaluates to 2
           [:span {:style {:color "blue"}} "2"]))
    (is (= (-> (get segments 3) :out)
           ; "(println \"hello\")" gives :out hello
           "hello\n"))))

(comment
  (eval-notebook this "test.notebook.apple")
  (load-notebook this "test.notebook.apple")
  ;(loadr :edn "demo/rdocument/demo/notebook_test/apple/notebook.edn")

;  
  )

(ns reval.document.notebook-eval-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.notebook.store :refer [fpath url-root get-link-ns get-path-ns]]
   [reval.notebook :refer [eval-notebook load-notebook]]
   [reval.test-init]
   [reval.config :refer [configure-reval]]))

(configure-reval {:rdocument  {:fpath "/tmp/rdocument"
                               :rpath "/api/rdocument/file"
                               :url-root "/api/rdocument/file/"}
                  :namespace-root ["notebook" "user" "demo"]
                  :clones-root ".reval/clones"})

(def this reval.config/reval)

(deftest config-test
  (let [rpath-val (fpath this)
        url-root-val (url-root this)
        path-ns (get-path-ns this 'demo.apple)]
    (is (= rpath-val "/tmp/rdocument"))
    (is (= url-root-val "/api/rdocument/file"))
    (is (= path-ns "/tmp/rdocument/demo/apple"))))

(deftest notebook-eval-test
  (eval-notebook  "test.notebook.apple")
  (let [nb  (load-notebook  "test.notebook.apple")
        nb (:data nb)
        segments (:content nb)]
    (is (= (get-in nb [:meta :ns]) "test.notebook.apple"))
    (is (= (count segments) 6))
    (is (= (-> (get segments 0) :result :value :data)
           ; (ns ) evaluates to nil.
           [:span {:class "clj-nil"} "nil"]
           ))
    (is (= (-> (get segments 1) :result :value :data)
           ; (+ 1 1) evaluates to 2
           [:span {:class "clj-long"} "2"]))
    (is (= (-> (get segments 3) :out)
           ; "(println \"hello\")" gives :out hello
           "hello\n"))))

(comment
  (eval-notebook  "test.notebook.apple")
  (-> (load-notebook  "test.notebook.apple")
      :data
      :content
      ;(get 0)
      (get 1)
      :result
      :value
      :data
      )
  

;  
  )

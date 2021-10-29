(ns reval.type.clj-type-test
  (:require
   [clojure.test :refer :all]
   [reval.type.protocol :refer [to-hiccup]]
   [reval.type.clj] ; bring the renderers into scope
   ))

; Type Tests	array? fn? number? object? string?
; instance?
; 	fn?  ifn?

(deftest renderable-nil
  (is (= (to-hiccup nil)
         [:span {:class "clj-nil"} "nil"])))

(deftest renderable-keyword
  (is (= (to-hiccup :test)
         [:span {:class "clj-keyword"} ":test"])))

(deftest renderable-symbol
  (is (= (to-hiccup (symbol "s"))
         [:span {:class "clj-symbol"} "s"])))

(deftest renderable-string
  (is (= (to-hiccup "s")
         [:span {:class "clj-string"} "\"s\""])))

(deftest renderable-char
  (is (= (to-hiccup \c)
         [:span {:class "clj-char"} "\\c"])))

(deftest renderable-number
  (is (= (to-hiccup 13)
         [:span {:class "clj-long"} "13"])))

(deftest renderable-bool
  (is (= (to-hiccup true)
         [:span {:class "clj-boolean"} "true"])))

;; awb99: I am too lazy to implement this test, especially since the
;; list-alike rendering needs refactoring
#_(deftest renderable-map
    (is (= (to-hiccup {:a 1 :b 2})
           {:type :hiccup
            :content "<span class='cljs-map'>true</span>"
            ;:value "true"
            })))

(defrecord MyRecord [r])

;; TODO awb99: this works on cljs, but not on clj.

#_(deftest renderable-catch-all
    (let [u (MyRecord. 3)]
  ;(println "type is: " (type u))
      (is (= (to-hiccup u)
             {:type :hiccup
              :content [:span {:class "clj-unknown"} "#pinkgorilla.ui.core-test.MyRecord{:r 3}"]
              :value "#pinkgorilla.ui.core-test.MyRecord{:r 3}"}))))
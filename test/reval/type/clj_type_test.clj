(ns reval.type.clj-type-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.type.protocol :refer [to-hiccup]]
   [reval.test-init]))

; Type Tests	array? fn? number? object? string?
; instance?
; 	fn?  ifn?

(deftest renderable-nil
  (is (= (to-hiccup nil)
         [:span {:style {:color "grey"}} "nil"])))

(deftest renderable-keyword
  (is (= (to-hiccup :test)
         [:span {:style {:color "rgb(30, 30, 82)"}} ":test"])))

(deftest renderable-symbol
  (is (= (to-hiccup (symbol "s"))
         [:span {:style {:color "steelblue"}} "s"])))

(deftest renderable-string
  (is (= (to-hiccup "s")
         [:span {:style {:color "grey"}} "\"s\""])))

(deftest renderable-char
  (is (= (to-hiccup \c)
         [:span {:style {:color "dimgrey"}} "\\c"])))

(deftest renderable-number
  (is (= (to-hiccup 13)
         [:span {:style {:color "blue"}} "13"])))

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
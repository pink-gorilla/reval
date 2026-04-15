(ns reval.type.clj-type-test
  (:require
   [clojure.test :refer [deftest is]]
   [dali.type.protocol :refer [to-dali]]
   [reval.test-init]))

; Type Tests	array? fn? number? object? string?
; instance?
; 	fn?  ifn?
(defn to-dali-data [v]
  (-> v (to-dali) :data))

(deftest renderable-nil
  (is (= (to-dali-data nil)
         [:span {:class "clj-nil"} "nil"])))

(deftest renderable-keyword
  (is (= (to-dali-data :test)
         [:span  {:class "clj-keyword"} ":test"])))

(deftest renderable-symbol
  (is (= (to-dali-data (symbol "s"))
         [:span {:class "clj-symbol"} "s"])))

(deftest renderable-string
  (is (= (to-dali-data "s")
         [:span  {:class "clj-string"} "\"s\""])))

(deftest renderable-char
  (is (= (to-dali-data \c)
         [:span {:class "clj-char"} "\\c"])))

(deftest renderable-number
  (is (= (to-dali-data 13)
         [:span {:class "clj-long"} "13"])))

(deftest renderable-bool
  (is (= (to-dali-data true)
         [:span {:class "clj-boolean"} "true"])))

;; awb99: I am too lazy to implement this test, especially since the
;; list-alike rendering needs refactoring
#_(deftest renderable-map
    (is (= (to-dali-data {:a 1 :b 2})
           {:type :hiccup
            :content "<span class='cljs-map'>true</span>"
            ;:value "true"
            })))

(defrecord MyRecord [r])

;; TODO awb99: this works on cljs, but not on clj.

#_(deftest renderable-catch-all
    (let [u (MyRecord. 3)]
  ;(println "type is: " (type u))
      (is (= (to-dali-data u)
             {:type :hiccup
              :content [:span {:class "clj-unknown"} "#pinkgorilla.ui.core-test.MyRecord{:r 3}"]
              :value "#pinkgorilla.ui.core-test.MyRecord{:r 3}"}))))


(comment 
  (to-dali-data "d")
  
 ; 
  )
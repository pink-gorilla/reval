(ns reval.type.custom-test
  (:require
   [clojure.test :refer [deftest is]]
   [dali.type.protocol :refer [to-dali dali-convertable]]
   [reval.test-init]))

;; REIFY DUMMY

(def quick-foo
  (reify dali-convertable
    (to-dali [this] "quick-foo")))

(deftest reify-dummy
  (is (= (to-dali quick-foo) "quick-foo")))

;; CUSTOM RENDERER

(defrecord Bongo [v])

(extend-type Bongo
  dali-convertable
  (to-dali [this]
    [:p (:v this)]))

(deftest renderable-custom
  (is (= (to-dali (Bongo. 7))
         [:p 7])))

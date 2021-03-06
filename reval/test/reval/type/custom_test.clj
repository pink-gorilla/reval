(ns reval.type.custom-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.type.protocol :refer [to-hiccup hiccup-convertable]]
   [reval.test-init]))

;; REIFY DUMMY

(def quick-foo
  (reify hiccup-convertable
    (to-hiccup [this] "quick-foo")))

(deftest reify-dummy
  (is (= (to-hiccup quick-foo) "quick-foo")))

;; CUSTOM RENDERER

(defrecord Bongo [v])

(extend-type Bongo
  hiccup-convertable
  (to-hiccup [this]
    [:p (:v this)]))

(deftest renderable-custom
  (is (= (to-hiccup (Bongo. 7))
         [:p 7])))

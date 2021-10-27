(ns pinkgorilla.gorilla-plot.pinkie-test
  (:require
   [cljs.test :refer-macros [deftest is]]
   [pinkgorilla.vega.impl.react]))

; make sure it compiles

(deftest pinkie-compile
  (is (= 0 0)))
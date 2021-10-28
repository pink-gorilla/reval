(ns pinkgorilla.gorilla-plot.util-test
  (:require
   #?(:cljs [cljs.test :refer-macros [deftest is]]
      :clj  [clojure.test :refer [deftest is]])
   [pinkgorilla.vega.plot.util :refer [gen-uuid]]))

(deftest uuid-not-nil
  (is (not (nil? (gen-uuid)))))


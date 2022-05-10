(ns reval.kernel.clj-eval-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.kernel.clj-eval :refer [clj-eval]]))

(deftest clj-eval-test
  (let [er1 (clj-eval {:code "(println 3) (def x 777) (defn f [] 99) (+ 3 4)"
                       :ns "bongotrott"
                       :id 1})
        er2 (clj-eval {:code "x"
                       :ns "bongotrott"
                       :id 2})
        er3 (clj-eval {:code "(f)"
                       :ns "bongotrott"
                       :id 3})]
    ; check that eval result is correct
    (is (= (:value er1) 7))
    (is (= (:out er1) "3\n"))
    (is (= (:ns er1) "bongotrott"))
    ; check that the namespace set/unset works
    (is (= (:value er2) 777))
    ;(is (= (:value er3) 'bongotrott/y))
    (is (= (:value er3) 99))))


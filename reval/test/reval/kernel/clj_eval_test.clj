(ns reval.kernel.clj-eval-test
  (:require
   [clojure.test :refer [deftest is]]
   [promesa.core :as p]
   [reval.kernel.clj-eval :refer [clj-eval]]
   [reval.kernel.protocol :refer [kernel-eval]]))

(deftest clj-eval-test
  (let [er1 (p/await (kernel-eval {:code "(ns bongotrott) (println 3)(def x 777) (defn f [] 99) (+ 3 4)"
                                   ;"(println 3) (def x 777) (defn f [] 99) (+ 3 4)"
                                   :kernel :clj
                                   :ns "bongotrott"
                                   :id 1}))
        er2 (p/await (kernel-eval {:code "x"
                                   :kernel :clj
                                   :ns "bongotrott"
                                   :id 2}))
        er3 (p/await (kernel-eval {:code "(f)"
                                   :kernel :clj
                                   :ns "bongotrott"
                                   :id 3}))]
    ; check that eval result is correct
    (is (= (:value er1) 7))
    (is (= (:out er1) "3\n"))
    (is (= (:ns er1) "bongotrott"))
    ; check that the namespace set/unset works
    (is (= (:value er2) 777))
    ;(is (= (:value er3) 'bongotrott/y))
    (is (= (:value er3) 99))))


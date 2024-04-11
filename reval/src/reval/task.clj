(ns reval.task
  (:require
   [reval.document.collection  :refer [nb-collections eval-collections]]
   [reval.default] ; side-effects
   [clojure.pprint :refer [print-table]]))

(defn eval-all-collections [m]
  (println "evaluating nb collections .. m: " (keys m))
  (let [cols (nb-collections)]
    (println "nb-cols: " cols)
    (eval-collections cols)))


(defn inline-coll [[cname coll]]
  (let [x (partition 2 coll)
        y (map (fn [[kernel nbs]]
                 (map #(assoc % :kernel kernel :coll cname) nbs)) x)]
    (apply concat y)))

(defn inline-collections [cols]
  (reduce concat [] (map inline-coll cols)))


(defn print-all-collections [m]
  (println "nb collections .. m: " (keys m))
  (let [cols (nb-collections)
        cols-f (inline-collections cols)]
    (print-table [:coll :nbns :ext :kernel :path] cols-f)))


(comment
  (inline-collections (nb-collections))
  (print-all-collections {})


 ; 
  )


  


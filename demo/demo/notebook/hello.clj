(ns demo.notebook.hello
  (:require
   [clojure.pprint :refer [print-table]]))

(+ 1 1)

[1 2 3]

(println "hello")

(defn add3 [v]
  (+ 3 v))

(def movies
  [{:name "Terminator" :year 1984 :studio "Hemdale" :producer "Gale Anne Hurd"}
   {:name "Men in Black" :year 1997 :studio "Amblin Entertainment" :producer "Walter F. Parkes"}
   {:name "Matrix" :year 1999 :studio "Warner Bros" :producer "Joel Silver"}
   {:name "Dr. Strange" :year 2016 :studio "Marvel Studios" :producer "Scott Derrickson"}])

(print-table movies)


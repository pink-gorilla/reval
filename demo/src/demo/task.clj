(ns demo.task
  (:require
   [reval.core :refer [*env* eval-collections]]))

(defn eval-all [& _]
  (eval-collections *env*))

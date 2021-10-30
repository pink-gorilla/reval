(ns reval.kernel.protocol
  (:require
   [reval.helper.id :refer [guuid]]
   #?(:clj [clojure.core.async :refer [>!  chan close! go]]
      :cljs [cljs.core.async  :refer [>! chan close!]
             :refer-macros [go]])))

#?(:clj (defmulti kernel-eval (fn [e] (:kernel e)))
   :cljs (defmulti kernel-eval (fn [e] (:kernel e))))

(defmethod kernel-eval :default [m]
  (let [c (chan)]
    (go (>! c {:id (guuid)
               :err (str "kernel unknown: " (:kernel m))})
        (close! c))
    c))

(defn available-kernels []
  (->> (methods kernel-eval)
       keys
       (remove #(= :default %))
       (into [])))

(comment

  (available-kernels)

 ; 
  )
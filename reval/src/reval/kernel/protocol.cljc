(ns reval.kernel.protocol
  (:require
   [modular.helper.id :refer [guuid]]
   [promesa.core :as p]))

#?(:clj (defmulti kernel-eval (fn [e] (:kernel e)))
   :cljs (defmulti kernel-eval (fn [e] (:kernel e))))

(defmethod kernel-eval :default [{:keys [id code ns kernel]
                                  :or {id (guuid)}}]
  (p/resolved
   {:id (guuid)
    :code code
    :err (str "kernel unknown: " kernel)}))


(defn available-kernels []
  (->> (methods kernel-eval)
       keys
       (remove #(= :default %))
       (into [])))

(comment

  (available-kernels)

  (-> (kernel-eval {:code "(+ 7 7)" :kernel :minister-clj})
      (p/then (fn [r]
                (println "result: " r))))

  (-> (kernel-eval {:code "(+ 7 7)" :kernel :clj})
      (p/then (fn [r]
                (println "result: " r))))
 

; 
  )
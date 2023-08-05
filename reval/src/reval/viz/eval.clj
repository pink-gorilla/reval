(ns reval.viz.eval
  (:require
   [reval.viz.data :refer [value->data]]
   [reval.kernel.clj-eval :refer [clj-eval-raw clj-eval]]))

(defn viz-eval [{:keys [code ns]}]
  (let [{:keys [err value] :as er}
        ;(clj-eval-raw code)
        (clj-eval {:code code :ns ns})]

    (if err
      er
      (->  er
           (dissoc :value)
           (merge  (value->data value))))))

(comment
  (viz-eval {:code "(/ 1 3)"})
  (viz-eval {:code "(/ 1 0)"})
  (clj-eval-raw "(/ 1 0)")
;
  )

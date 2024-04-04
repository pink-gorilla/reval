(ns reval.task
  (:require
   [reval.config :refer [default-reval-config set-config!]]
   [reval.document.collection  :refer [nb-collections eval-collections]]
   [reval.default] ; side-effects
   ))

(defn nbeval
  ([{:keys [config]; a map so it can be consumed by tools deps -X
     :or {config default-reval-config}
     :as p}]
    (set-config! config)
   (nbeval))
  ([]
   (-> (nb-collections)
       eval-collections)))


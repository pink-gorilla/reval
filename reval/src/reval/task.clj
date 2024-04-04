(ns reval.task
  (:require
   [reval.config :refer [default-reval-config set-config!]]
   [reval.document.collection  :refer [nb-collections eval-collections]]
   [reval.default] ; side-effects
   [modular.config]
   ))

(defn nbeval
  ([{:keys [config]; a map so it can be consumed by tools deps -X
     :or {config default-reval-config}
     :as p}]
   (modular.config/load-config! config)
   (println "setting reval config: " config)
   (println "full config: " (modular.config/get-in-config []))
    (set-config! (modular.config/get-in-config [:reval]))
   (nbeval))
  ([]
   (println "evaluating nb collections ..")
   (let [cols (nb-collections)]
     (println "nb-cols: " cols)
     (eval-collections cols)  )))


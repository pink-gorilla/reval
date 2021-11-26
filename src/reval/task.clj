(ns reval.task
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [modular.log :refer [timbre-config!]]
   [modular.config :refer [config-atom get-in-config load-config!]]
   [reval.document.collection  :refer [nb-collections eval-collections]]
   [reval.default] ; side-effects
   ))

(def default-reval-config
  {:timbre-loglevel [[#{"*"} :info]]
   :reval {:rdocument {:storage-root "demo/rdocument/"
                       :url-root "/api/rdocument/file/"}
           :collections {:user [:clj "user/notebook/"]
                         :demo [:clj "demo/notebook/"]
                         :demo-cljs [:cljs "demo/notebook/"]}}})

(defn nbeval
  ([{:keys [config]; a map so it can be consumed by tools deps -X
     :or {config default-reval-config}
     :as p}]
   (if p
     (do (info "using user config")
         (load-config! config))
     (do (info "using default config!")
         (reset! config-atom default-reval-config)))
   (timbre-config! @config-atom)
   (nbeval))
  ([]
   (-> (nb-collections)
       eval-collections)))


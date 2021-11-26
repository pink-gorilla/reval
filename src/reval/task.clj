(ns reval.task
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [modular.log :refer [timbre-config!]]
   [modular.config :refer [config-atom get-in-config]]
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

(defn init-config! []
  (if (get-in-config [:reval])
    (info "using user config")
    (do
      (info "using default config!")
      (reset! config-atom default-reval-config)
      (timbre-config! @config-atom))))

(defn nbeval
  ([config]
   (init-config!)
   (nbeval))
  ([]
   (-> (nb-collections)
       eval-collections)))




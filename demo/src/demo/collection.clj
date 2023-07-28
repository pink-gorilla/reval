(ns demo.collection
  (:require
   [modular.config :as config]
   [reval.document.notebook :refer [eval-notebook]]
   [reval.document.collection :refer [nb-collections]]))

(config/set!
 :reval
 {:rdocument {:storage-root "/tmp/rdocument/"
              :url-root "/api/rdocument/file/"}
  :collections {:demo [:clj "demo/notebook/"]
                :user [:clj "notebook/big_list"]}})

(nb-collections)

(ns reval.test-init
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [modular.log :refer [timbre-config!]]

   [reval.default]    ;; side effect: to-hiccup value converter, persister loaded
   [reval.kernel.clj-eval] ;; side effect: add clj kernel
   ))

(timbre-config!
 {:timbre-loglevel [[#{"*"} :info]]})

(info "setting reval test config..")

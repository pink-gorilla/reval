(ns reval.test-init
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [modular.log :refer [timbre-config!]]
   [modular.config :as config]
   [reval.default]    ;; side effect: to-hiccup value converter, persister loaded
   [reval.kernel.clj-eval] ;; side effect: add clj kernel
   ))

(timbre-config!
 {:timbre-loglevel [[#{"*"} :info]]})

(info "setting reval test config..")

(config/set!
 :reval
 {:rdocument {:storage-root "/tmp/rdocument/"
              :url-root "/api/rdocument/file/"}
  :collections {:demo [:clj "demo/notebook/"]
                :user [:clj "demo/notebook_test/"]}})


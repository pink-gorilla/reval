(ns reval.test-init
  (:require
   [taoensso.timbre :as timbre]
   [reval.config :refer [use-tmp]]
   [reval.default]    ;; side effect: to-hiccup value converter, persister loaded
   [reval.kernel.clj-eval] ;; side effect: add clj kernel
   ))

(println "timbre loglevel: info")
(timbre/set-config!
 (merge timbre/default-config
        {:min-level :info}))

(use-tmp)




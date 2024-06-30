(ns reval.test-init
  (:require
   [modular.log :refer [timbre-config!]]
   [reval.default]    ;; side effect: to-hiccup value converter, persister loaded
   [reval.kernel.clj-eval] ;; side effect: add clj kernel
   ))

(timbre-config!
 {:timbre-loglevel [[#{"*"} :info]]})


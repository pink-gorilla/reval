(ns reval.init
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadowx.module.build :refer [load-namespace]]))

(defn reval-cljs-kernel-init [_config]
  (info "reval cljs-kernel-init ..")
  (load-namespace 'reval.kernel.protocol)
  (load-namespace 'reval.kernel.cljs-sci)
  nil)

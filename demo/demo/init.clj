(ns demo.init
 (:require
   [taoensso.timbre :as timbre]
   [reval.document.notebook]
   [reval.config :as c]
   [goldly.devtools] ; side effects
  ))

(timbre/set-config!
 (merge timbre/default-config
        {:min-level :info}))


(c/set-config!
 {:storage-root "demo/rdocument/"
  :url-root "/api/rdocument/file/"})
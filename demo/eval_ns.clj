(ns demo.eval-ns
  (:require
   [taoensso.timbre :as timbre]
   [reval.ns-eval :refer [eval-ns]]
   [reval.config :as c]))

(timbre/set-config!
 (merge timbre/default-config
        {:min-level :info}))

(c/set-config!
 {:storage-root "demo/rdocument/"
  :url-root "/api/rdocument/"})



(eval-ns "demo.notebook.apple")

(-> (eval-ns "demo.notebook.apple")
    :content
    count)

(eval-ns "demo.notebook.image")










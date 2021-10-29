(ns demo.eval-ns
  (:require
   [taoensso.timbre :as timbre]
   [reval.ns-eval :refer [eval-ns]]
   [reval.config :as c]
   [demo.init] ; side effects
   ))


(eval-ns "demo.notebook.apple")

(-> (eval-ns "demo.notebook.apple")
    :content
    count)

(eval-ns "demo.notebook.image")

(eval-ns "demo.notebook.banana")










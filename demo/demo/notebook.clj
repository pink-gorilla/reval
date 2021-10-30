(ns demo.notebook
  (:require
   [taoensso.timbre :as timbre]
   [reval.document.notebook :refer [eval-notebook]]
   [reval.config :as c]
   [demo.init] ; side effects
   ))


(eval-notebook "demo.notebook.apple")

(-> (eval-notebook "demo.notebook.apple")
    :content
    count)

(eval-notebook "demo.notebook.image")

(eval-notebook "demo.notebook.banana")










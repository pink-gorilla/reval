(ns demo.eval-ns
  (:require
   [taoensso.timbre :as timbre]
   [reval.ns-eval :refer [eval-ns]]
   [reval.config :as c]))

 (timbre/set-config!
   (merge timbre/default-config
         {:min-level :info}))


 (c/use-project)

  

 (eval-ns "notebook.apple")


 (-> (eval-ns "notebook.image")
     :content
     count 
  )
 


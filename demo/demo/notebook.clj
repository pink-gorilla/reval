(ns demo.notebook
  (:require
   [reval.document.notebook :refer [eval-notebook]]
   [goldly.scratchpad :refer [show! show-as clear!]]
   [demo.init] ; side effects
   ))


(-> (eval-notebook "user.notebook.hello")
    meta)


(-> (eval-notebook "user.notebook.hello")
    :content
    count)

(->> (eval-notebook "user.notebook.hello")
     (show-as :p/notebook))

; demo.notebook.image is part of the reval demo notebooks
(->> (eval-notebook "user.notebook.image")
     (show-as :p/notebook))

(->> (eval-notebook "user.notebook.hello")
     show!)































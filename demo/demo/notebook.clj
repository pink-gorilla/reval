(ns demo.notebook
  (:require
   [reval.document.notebook :refer [eval-notebook]]
   [goldly.scratchpad :refer [show! show-as clear!]]
   [demo.init] ; side effects
   ))


(eval-notebook "demo.notebook.hello")

(-> (eval-notebook "demo.notebook.hello")
    :content
    count)

(->> (eval-notebook "demo.notebook.hello")
     (show-as :p/notebook))

; demo.notebook.image is part of the reval demo notebooks
(->> (eval-notebook "demo.notebook.image")
     (show-as :p/notebook))


















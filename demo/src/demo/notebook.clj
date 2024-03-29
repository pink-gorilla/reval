(ns demo.notebook
  (:require
   [reval.document.notebook :refer [eval-notebook load-notebook]]
   ;[scratchpad.core :refer [show! show-as clear!]]
  ; [demo.init] ; side effects
   ))

;; checkout one notebook..

(-> (eval-notebook "notebook.study.movies")
    meta)

(-> (eval-notebook "user.notebook.movies")
    :content
    count)

(->> (eval-notebook "user.notebook.movies")
     (show-as :p/notebook))

(load-notebook "user.notebook.movies")

(-> (load-notebook "user.bongo.xyr")
    println)

(-> (load-notebook "")
    println)

(-> (load-notebook nil)
    println)

;; eval an notebook that does not exist:

(->> (eval-notebook "user.notebook.image") ; this does not exist !!
     (show-as :p/notebook)) ;; we will get a notebook that contains an error.

;; eval a list of notebooks

(map eval-notebook ["user.notebook.movies"
                    "user.notebook.exception"
                    "demo.notebook.reval-image" ;; demo.notebook.image is part of the reval demo notebooks
                    "demo.notebook.highlightjs"])

;; show notebook in scratchpad

(->> (eval-notebook "user.notebook.movies")
     show!)



































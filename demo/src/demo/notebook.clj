(ns demo.notebook
  (:require
   [modular.system]
   [reval.document.notebook :refer [eval-notebook load-notebook]]
   ;[scratchpad.core :refer [show! show-as clear!]]
  ; [demo.init] ; side effects
   ))
  
;; first lets get the running reval  instance
(def s (modular.system/system :reval))


 
;; checkout one notebook..

(eval-notebook s "notebook.study.movies")


(-> (eval-notebook s "notebook.study.movies")
    meta)



(-> (eval-notebook s "user.notebook.movies")
    :content
    count)

(->> (eval-notebook s "user.notebook.movies")
     (show-as :p/notebook))

(load-notebook s "user.notebook.movies")

(-> (load-notebook s "user.bongo.xyr")
    println)

(-> (load-notebook s "")
    println)

(-> (load-notebook s nil)
    println)

;; eval an notebook that does not exist:

(->> (eval-notebook s "user.notebook.image") ; this does not exist !!
     (show-as :p/notebook)) ;; we will get a notebook that contains an error.

;; eval a list of notebooks

(map eval-notebook ["user.notebook.movies"
                    "user.notebook.exception"
                    "demo.notebook.reval-image" ;; demo.notebook.image is part of the reval demo notebooks
                    "demo.notebook.highlightjs"])

;; show notebook in scratchpad

(->> (eval-notebook s "user.notebook.movies")
     show!)



































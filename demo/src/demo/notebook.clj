(ns demo.notebook
  (:require
   [reval.core :refer [*env* eval-collections]]
   [reval.document.collection :refer [eval-collection]]
   [reval.document.notebook :refer [eval-notebook load-notebook create-notebook]]))

*env*

;; checkout one notebook..

(eval-notebook *env* "notebook.study.movies")
(load-notebook *env* "notebook.study.movies")

(eval-notebook *env* "notebook.test27.exception")

(eval-notebook *env* "notebook.study.image")

(create-notebook *env* "notebook.cljs.cljs" :cljs)

(create-notebook *env* "notebook.study.image")

; notebook collection
(eval-collection
 *env*
 {:clj ["notebook/study/"
        "demo/notebook/"
        ;"notebook/big_list/"
        ]
  :cljs ["notebook/cljs/"
         "demo/notebook/"
         ]})



(eval-collections *env*)


(load-notebook *env* "user.bongo.xyr")

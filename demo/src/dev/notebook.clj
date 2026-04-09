(ns dev.notebook
  (:require
   [reval.notebook :refer [eval-notebook load-notebook create-notebook]]
   [reval.collection :refer [eval-collection eval-build-collections]]))


;; notebook

(create-notebook "notebook.cljs.cljs" :cljs)
(create-notebook "notebook.study.image")

(load-notebook "user.bongo.xyr")

(eval-notebook "notebook.study.movies")
(load-notebook "notebook.study.movies")

(eval-notebook "notebook.test27.exception")

(eval-notebook "notebook.study.image")

(eval-notebook "notebook.dali.rtable.chart.highstock-ds-barcolor")



; collection

(eval-collection

 {:clj ["notebook/study/"
        "demo/notebook/"
        ;"notebook/big_list/"
        ]
  :cljs ["notebook/cljs/"
         "demo/notebook/"]})

(eval-build-collections
 {:study {:clj "notebook/study/"}
  ;:big-list {:clj "notebook/big_list/"}
  ;:cljs {:cljs "notebook/cljs/"}
  :demo {:clj "demo/notebook/" ; embedded notebooks in jars.
         :cljs "demo/notebook/"}})



(ns dev.notebook
  (:require
   [reval.notebook :refer [eval-notebook load-notebook create-notebook]]
   [reval.collection :refer [eval-collection eval-build-collections]]))

;; notebook
(create-notebook "notebook.reval-test.image")
(load-notebook "notebook.reval-test.image")

(eval-notebook "notebook.reval-test.movies")
(load-notebook "notebook.reval-test.movies")

(eval-notebook "notebook.reval-test.exception")

(eval-notebook "notebook.reval-test.image")

(eval-notebook "notebook.dali.rtable.chart.highstock-ds-barcolor")

(eval-notebook "quanta.notebook.asset-db.eodhd-asset-db")
(load-notebook "quanta.notebook.asset-db.eodhd-asset-db")

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



(ns reval.notebook-ui.eval-error
  ;(:require  [goldly :refer [error-view]])
  
  )

 ;[goldly :refer [error-view]]


(defn error-view [e]
  [:div (pr-str e)]
  )


(defn evalerr-sci [err-sci]
  ;[error-view "" err-sci]
   [:div (pr-str err-sci)])
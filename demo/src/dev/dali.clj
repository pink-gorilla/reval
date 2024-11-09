(ns dev.dali
  (:require
   [modular.system]
   [dali.spec :refer [dali-spec?]]
   [reval.type.converter :refer [type->dali]]
   [reval.dali.plot.type :refer [list->dali]]))

;; first lets get the running reval  instance
(def s (modular.system/system :reval))

s

(type->dali nil 1)
(type->dali nil nil)

(type->dali nil "asdf")

(type->dali nil [3 4])
(type->dali nil '(3 4))

(type->dali nil 'notebook.study.movies/more-movies)

(list->dali nil {:separator ""} [1 :yes "a"])
(list->dali nil {} {:a 1 :b "BB"})

(dali-spec? {:a 1 :b "BB"})

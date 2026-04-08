(ns dev.dali
  (:require
   [modular.system]
   [dali.spec :refer [dali-spec?]]
   [reval.type.converter :refer [type->dali]]
   [reval.dali.plot.type :refer [list->dali]]))

;; first lets get the running reval  instance
(def s (modular.system/system :reval))

s

(type->dali  1)
(type->dali  nil)

(type->dali  "asdf")

(type->dali  [3 4])
(type->dali  '(3 4))

(type->dali  'notebook.study.movies/more-movies)

(list->dali  {:separator ""} [1 :yes "a"])
(list->dali  {} {:a 1 :b "BB"})

(dali-spec? {:a 1 :b "BB"})

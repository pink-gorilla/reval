(ns src.dev.repl
  (:require
   [reval.kernel.clj-eval :refer [clj-eval-raw]])
  (:import
   [clojure.lang Compiler$CompilerException]))

(def code1
  "(ns notebook.dali.rtable.chart.highstock-ds-bar
  (:require
   [clojure.io :as io]
   [transit.io :refer [decode]]
   [rtable.plot :as plot]
   [notebook.dali.random-bars :refer [random-bar-ds]]))")

(def r (clj-eval-raw code1))
(ex-message (:ex r))

(def r2 (clj-eval-raw "(+ 1 a)"))

(ex-message (:ex r2))


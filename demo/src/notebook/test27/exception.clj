(ns notebook.test27.exception)

; throw an exception !

(def a 34)

(str *ns*) ; this should be the notebook ns

(println hello) ; hello should be a string

; the following code is important
; nb-eval needs to be able to continue after
; one exception.
(+ 5 5 5 5 5 5)
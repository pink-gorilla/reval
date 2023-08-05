(ns notebook.test.exception)

; throw an exception !

(def a 34)

(str *ns*) ; this should be the notebook ns

(println hello) ; hello should be a string

; the following code is important
; nb-eval needs to be able to continue after
; one exception.
(+ 5 5 5 5 5 5)

(with-meta {:name "Wolfgang"}
  {:render-fn 'notebook.test27.greeter/greet})

(with-meta
  ['notebook.test27.greeter/greet {:name "Wolfgang"}]
  {:hiccup true})

; TODO-FIXME - this should work - problem in escaping.
(with-meta
  [:p.text-red-500.text-bold "Hello, Hiccup!"]
  {:hiccup true})

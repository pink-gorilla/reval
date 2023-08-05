(ns notebook.test.text)

; seqs and maps are not implemented
; the question is what is an efficient show way that is also nice.
(+ 1 1)
[1 2 3]

^:R
['reval.goldly.ui-helper/text2 "hello\nworld"]

^:R
['notebook.test27.greeter/greet {:name "Wolfgang"}]

(with-meta
  {:name "Harry Potter"}
  {:render-fn 'notebook.test27.greeter/greet})

; this is important in case the data we want to pass is
; a string, and strings cannot have meta-data.
(with-meta
  {:data {:name "Peter Pan"}}
  {:render-fn-escaped 'notebook.test27.greeter/greet})
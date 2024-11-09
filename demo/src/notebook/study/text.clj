(ns notebook.study.text
  (:require
   [dali.plot.hiccup :refer [hiccup]]
   [dali.plot.text :refer [text]]
   [dali.spec :refer [create-dali-spec]]))

; seqs and maps are not implemented
; the question is what is an efficient show way that is also nice.
(+ 1 1)
[1 2 3]

(println "a\nb\nc\nddddd")

(text {:text "hello\nworld"})

(hiccup
 [:h1.text-green-600 "Just Hickup"])

(defn greet [data]
  (create-dali-spec
   {:viewer-fn 'demo.greeter/greet
    :data data}))

(greet {:name "Wolfgang"})

(greet {:name "Harry Potter"})

(greet {:name "Peter Pan"})

(greet {:name "Dschingis Kahn"})







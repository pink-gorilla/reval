(ns notebook.cljs.cljs)

(def a 34)

25
:yes

(->> (range 100)
     (map inc))

'(3 4 5)

; println does not yet work.
;(println "hello\nworld!")

(defn add3 [v]
  (+ 3 v))


(println "a\nb\nc\ndd\r\nddd")

(-> (with-out-str 
     (println "a\nb\nc\ndd\r\nddd"))
    text2)
  

(require '[reval.goldly.ui-helper :refer [text2]])

(-> "a\nb\nc" text2 )

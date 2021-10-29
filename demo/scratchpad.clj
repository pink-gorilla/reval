(ns demo.scratchpad
  (:require
   [goldly.scratchpad :refer [show! clear!]]
   [reval.ui :as ui]))


(show! [:p "hello, demo!"])


; evaled expression inside the hiccup.
(show! [:p "Multiplication result: " (* 7 7)])

; this goes to the htmp code tag!
(show! [:p/code "(println 5) ; this is code"])


; show pinkie tags!
(show! [:p/clock])

; (show! (ui/image ) )

(defmacro cljs [render-fn & args]
  (into
   [(str render-fn)]
   args))



(macroexpand 
 (cljs2 :code "(println 5)"))







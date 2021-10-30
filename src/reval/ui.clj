(ns reval.ui
  (:require
   [reval.type.image :as i]))

(def img-inline i/image-inline)

(def img i/image)

; we really need a macro that gets a symbol and returns 
; a function that returns ['symbol & args]

#_(defmacro cljs2 [render-fn & args]
    (into
     [(quote `render-fn)]
     args))

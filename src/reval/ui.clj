(ns reval.ui
  (:require
   [reval.type.image :as i]))

(def img-inline i/image-inline)

(def img i/image)

(defmacro cljs2 [render-fn & args]
  (into
   [(quote `render-fn)]
   args))

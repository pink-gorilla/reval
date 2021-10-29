(ns notebook.image
  (:require
   [clojure.java.io :as io]
   [reval.type.protocol :refer [to-hiccup]]
   [reval.ui :refer [img-inline img]]
   [reval.type.image :refer [load-img]]))

;; there is also a unit-test which does the same thing.


(-> "demo/resources/sun.png"
    io/file
    .exists)


(-> (load-img  "demo/resources/sun.png")
    img-inline
    ;to-hiccup
    )


(-> (load-img  "demo/resources/sun.png")
    img
    (assoc :alt "adf")
    ;type
    ;to-hiccup
    )






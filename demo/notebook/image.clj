(ns notebook.image
  (:require
   [clojure.java.io :as io]
   [reval.type.protocol :refer [to-hiccup]]
   [reval.persist.protocol :refer [loadr]]
   [reval.ui :refer [img-inline img]]))

;; there is also a unit-test which does the same thing.


(-> "demo/public/sun.png"
    io/file
    .exists)

(-> (loadr :png  "demo/public/sun.png")
    img-inline
    ;to-hiccup
    )


(-> (load-img  "demo/public/sun.png")
    img
    (assoc :alt "adf")
    ;type
    ;to-hiccup
    )



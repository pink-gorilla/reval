(ns notebook.test.reval-image
  (:require
   [clojure.java.io :as io]
   [modular.persist.protocol :refer [loadr]]
   [reval.ui :refer [img-inline img]]))

;; this notebook shows how you buffered images in clj
;; can be rendererd to a reproducible notebook.
;; load-img is just a helper function

(def iname "resources/public/sun.png")

(-> iname
    io/file
    .exists)

(-> (loadr :png iname)
    img-inline
    ;to-hiccup
    )
(-> (loadr :png iname)
    img
    (assoc :alt "adf")
    ;type
    ;to-hiccup
    )
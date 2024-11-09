(ns notebook.study.image
  (:require
   [clojure.java.io :as io]
   [modular.persist.protocol :refer [loadr]]
   [dali.plot.image :refer [image-inline image]]
   [reval.core :refer [*env*]]))

*env*

;; load an image from disk
;; loadr is just a helper function

(def iname "resources/public/sun.png")

(-> iname
    io/file
    .exists)

(def png
  (loadr :png iname))

png

;; this notebook shows how you buffered images in clj
;; can be renderered to a reproducible notebook.

(image-inline png {})

(image *env* {} png)

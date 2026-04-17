(ns notebook.reval-test.image
  (:require
   [clojure.java.io :as io]
   [dali.plot.image :refer [image-inline image]])
  (:import
   javax.imageio.ImageIO))

;; load an image from disk

(def file-name "resources/public/sun.png")

(-> file-name
    io/file
    .exists)

(def png
  (ImageIO/read (io/file file-name)))

png

;; this notebook shows how you buffered images in clj
;; can be renderered to a reproducible notebook.

(image-inline png {})

(image {} png)


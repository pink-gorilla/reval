(ns notebook.image
  (:refer-clojure :exclude [read])
  (:require
   [clojure.java.io :as io]
   [reval.type.hiccup :refer [to-hiccup]]
   [reval.ui :refer [img-inline img]])
  (:import
   [java.awt Image]
   [java.awt.image RenderedImage BufferedImageOp]
   [javax.imageio ImageIO ImageWriter ImageWriteParam IIOImage]
   [javax.imageio.stream FileImageOutputStream]))

;; there is also a unit-test which does the same thing.

(defn load-img
  "Reads a BufferedImage from source, something that can be turned into
  a file with clojure.java.io/file"
  [source]
  (ImageIO/read (io/file source)))


(-> "demo/resources/sun.png"
    io/file
    .exists)

(-> (load-img  "demo/resources/sun.png")
    img-inline
    to-hiccup)


(-> (load-img  "demo/resources/sun.png")
    img
    ;type
    to-hiccup)






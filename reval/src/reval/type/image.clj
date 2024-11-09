(ns reval.type.image
  "Render BufferedImage objects"
  (:require
   [dali.plot.image :refer [image-inline]]
   [reval.type.protocol :refer [dali-convertable]])
  (:import
   [java.awt.image BufferedImage]))


(extend-type BufferedImage
  dali-convertable
  (to-dali [v env]
    (image-inline v {})))
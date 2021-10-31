(ns reval.type.image
  "Render BufferedImage objects"
  (:require
   [clojure.java.io :as io]
   [clojure.data.codec.base64 :as b64]
   [clojure.string :as string]
   [reval.type.protocol :refer [hiccup-convertable to-hiccup hiccup-convertable-reproduceable]]
   [reval.document.manager :as rdm]
   [reval.helper.id :refer [guuid-str]])
  (:import
   [java.awt Image]
   [java.awt.image BufferedImage]
   [java.io ByteArrayOutputStream]
   [javax.imageio ImageIO]))

(defn image-to-bytes [^Image image ^String type width height]
  (let [bi (BufferedImage. width height (if (#{"png" "gif"} type)
                                          BufferedImage/TYPE_INT_ARGB
                                          BufferedImage/TYPE_INT_RGB))
        baos (ByteArrayOutputStream.)]
    (doto (.getGraphics bi) (.drawImage image 0 0 width height nil))
    (ImageIO/write bi type baos)
    (.toByteArray baos)))

;; inline image
(defrecord img-inline-record [image alt type width height]
  hiccup-convertable
  (to-hiccup [{:keys [image alt type width height]}]
    (let [b64-img (String. (b64/encode (image-to-bytes image type width height)))
          src (format "data:image/%1$s;base64,%2$s" type b64-img)]
      [:img {:src src
             :width width
             :height height
             :alt alt}])))

(defn image-inline [^BufferedImage image & {:keys [alt type width height]}]
  (let [alt (or alt "")
        type (string/lower-case (or type "png"))
        iw (.getWidth image)
        ih (.getHeight image)
        [w h] (cond
                (and width height) [(int width) (int height)]
                width [(int width) (int (* (/ width iw) ih))]
                height [(int (* (/ height ih) iw)) (int height)]
                :else [iw ih])]
    (img-inline-record. image alt type w h)))

;; image (to document manager)

(defrecord imgrecord [image alt type width height ns]
  hiccup-convertable
  (to-hiccup [{:keys [^BufferedImage image alt type width height]}]
    (let [name-no-ext (guuid-str)
          name (str name-no-ext "." type)
          src (rdm/get-link-ns ns name)]
      (rdm/save image ns name-no-ext :png)
      [:img {:src src
             :width width
             :height height
             :alt alt}])))

(defn image [^BufferedImage image & {:keys [alt type width height ns]}]
  (let [alt (or alt "")
        type (string/lower-case (or type "png"))
        iw (.getWidth image)
        ih (.getHeight image)
        [w h] (cond
                (and width height) [(int width) (int height)]
                width [(int width) (int (* (/ width iw) ih))]
                height [(int (* (/ height ih) iw)) (int height)]
                :else [iw ih])]
    (imgrecord. image alt type w h (str *ns*))))
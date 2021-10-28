(ns reval.type.image-test
  (:require
   [clojure.test :refer :all]
   [reval.type.hiccup :refer [to-hiccup]]
   [reval.ui.core :refer [img img-inline]])
  (:import java.io.File
           java.io.InputStream
           javax.imageio.ImageIO))

(defn remove-option [hiccup k]
  (let [[renderer opts & args] hiccup]
    (-> [renderer (dissoc opts k)]
        (concat args)
        vec)))

(deftest image-inline-test
  (let [sun (ImageIO/read (File. "demo/resources/sun.png"))
        hiccup (-> sun img-inline to-hiccup)
        hiccup-no-source (remove-option hiccup :src)]
    (is (= [:img {;:src "/api/viewer/user/6e914fe9-43b4-430e-82d8-59950ed024c5.png"
                  :width 192
                  :height 187
                  :alt ""}]
           hiccup-no-source))))

(deftest image-test
  (let [sun (ImageIO/read (File. "demo/resources/sun.png"))
        hiccup (-> sun img to-hiccup)
        hiccup-no-source (remove-option hiccup :src)]
    (is (= [:img {;:src "/api/viewer/user/6e914fe9-43b4-430e-82d8-59950ed024c5.png"
                  :width 192
                  :height 187
                  :alt ""}]
           hiccup-no-source))))

(comment

  (-> [:img {:src "x" :a 1} "hello"]
      (remove-option :src))

  (-> (ImageIO/read (File. "demo/resources/sun.png"))
      img
      to-hiccup
      (remove-option :src)))
(ns reval.document.collection
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [reval.document.classpath :refer [describe-url]]
   [resauce.core :as rs]))

(defn get-ns-files [res-path]
  (->> (rs/resource-dir res-path)
       (remove rs/directory?)
       (map (partial describe-url res-path))))

(defn split-ext [filename]
  (let [m (re-matches #"(.*)\.(clj[sc]*)" filename)
        [_ name ext] m]
    [name ext]))

(defn is-format? [fmt [_ ext]] ; name
  (case ext
    "cljs" (= fmt :cljs)
    "clj" (= fmt :clj)
    "cljc" true))

(defn filename->ns [dir name]
  (str
   (str/replace dir #"/" ".")
   (str/replace name #"_" "-")))

(defn get-ns-list [res-path fmt]
  (->> (get-ns-files res-path)
       (map :name)
       (map split-ext)
       (filter (partial is-format? fmt))
       (map first)
       (map #(filename->ns res-path %))))

; snippet notebook files should be in demo/notebook
; just "notebook" will create problems with notebook code namespace

(defn get-ns-overview []
  {:demo (get-ns-list "demo/notebook/" :clj)
   :user (get-ns-list "demo/notebook_test/" :clj)})

(comment

  (get-ns-files "demo/notebook")
  (get-ns-files "demo/notebook/")

  (split-ext "apple_blue.cljs")
  (split-ext "apple_blue.clj")
  (split-ext "apple_blue.cljc")

  (->> (split-ext "apple_blue.cljs")
       (is-format? :clj))
  (->> (split-ext "apple_blue.cljs")
       (is-format? :cljs))
  (->> (split-ext "apple_blue.cljc")
       (is-format? :cljs))
  (->> (split-ext "apple_blue.cljc")
       (is-format? :clj))

  (filename->ns "demo/notebook/" "apple_blue.clj")

  (get-ns-list "demo/notebook/" :clj)
  (get-ns-list "demo/notebook/" :cljs)

  (get-ns-overview)

  ;
  )

















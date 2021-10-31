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

(defn get-ns-list [fmt res-path]
  (->> (get-ns-files res-path)
       (map :name)
       (map split-ext)
       (filter (partial is-format? fmt))
       (map first)
       (map #(filename->ns res-path %))))

(defn get-nss-list [fmt res-paths]
  (->> (map #(get-ns-list fmt %) res-paths)
       (apply concat [])
       vec))

(defn get-collections [spec]
  (->> (map (fn [[k v]]
              [k (get-nss-list (first v) (rest v))]) spec)
       (into {})))

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

  (get-ns-list :clj "demo/notebook/")
  (get-ns-list :cljs "demo/notebook/")

  (get-nss-list :cljs ["demo/notebook/"])

  (get-collections
   {:demo [:clj "demo/notebook/"]
    :user [:clj "demo/notebook_test/"]})

;
  )

















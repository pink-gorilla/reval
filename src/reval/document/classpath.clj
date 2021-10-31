(ns reval.document.classpath
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [resauce.core :as rs])
  (:import
   [java.net JarURLConnection URI] ; URL
   ;[java.util.jar JarEntry]
   ))
; discover clj/cljs files in resources (can be jar or file)

(defn- url-scheme [url]
  ;; Using URI instead of URL to support arguments without schema.
  (.getScheme (URI. (str url))))

(defmulti url-name
  {:arglists '([url])}
  url-scheme)

(defmethod url-name "file" [url]
  (let [file (io/as-file url)]
    ;(.getPath file)
    (.getName file)))

(defmethod url-name "jar" [url]
  (let [conn (.openConnection url)
        path (.getEntryName ^JarURLConnection conn)]
    path))

(defn describe-url [res-path url]
  (let [scheme (url-scheme url)
        name (url-name url)
        ;  names for file: ("apple.clj" "banana.clj" 
        ;  names for jar:  "demo/notebook/site_template.cljs" "demo/notebook/ipsum_sidebar.cljs")
        ; so this needs to be corrected.
        name (if (= scheme "jar")
               (subs name  (if (str/ends-with? res-path "/")
                             (count res-path)
                             (inc (count res-path))))
               name) ; 
        ]
    {:scheme scheme
     :name name}))

(defn ns->dir [ns]
  (-> ns
      (str/replace #"\." "/")
      (str/replace #"\-" "_")))

(comment

  (-> "demo/notebook"
      (clojure.java.io/resource)
      (rs/directory?))

  (-> (rs/resources "demo/notebook/apple.clj")
      first
      (rs/directory?))

  (ns->dir "demo.notebook-test.apple")

;  
  )
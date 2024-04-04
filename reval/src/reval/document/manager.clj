(ns reval.document.manager
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [modular.persist.protocol :as p]
   [reval.document.classpath :refer [ns->dir]]
   [reval.config :refer [get-in-config]]))

; get-filename and get-link may NOT contain the fmt parameter
; the name contains the extension. The reason is, that we have
; a format detector based on full name. This is important, as
; formats that contain 2 extensions (.nippy.gz) might be hard
; to parse via a single regex, and therefore are not so easy
; to use in routing tables. 

; demo/rdocument/demo.notebook.apple/notebook.edn

;; URL side

(defn storage-root []
  (get-in-config [:reval :rdocument :storage-root]))

(defn url-root []
  (get-in-config [:reval :rdocument :url-root]))

(defn get-link-ns [ns name]
  (str (url-root)  ns "/" name))

;; FILE side

(defn get-path-ns [ns]
  (str (storage-root) (ns->dir ns) "/"))

(defn get-filename-ns [ns name]
  (str (get-path-ns ns) name))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn- ensure-directory-ns [ns]
  (let [root (storage-root)
        ns-path (ns->dir ns)
        dirs (str/split ns-path #"/")
        ensure (fn [r dir]
                 (let [edir (str r dir "/")]
                   (ensure-directory edir)
                   edir))]
    (ensure-directory root)
    (reduce ensure root dirs)))

(defn delete-recursively [fname]
  (doseq [f (reverse (file-seq (clojure.java.io/file fname)))]
    (clojure.java.io/delete-file f true)))

(defn delete-directory-ns [ns]
  (let [ns-path (get-path-ns ns)]
    (info "deleting reproduceable ns: " ns "path: " ns-path)
    (delete-recursively ns-path)))

(defn- add-extension [name format]
  (let [ext (clojure.core/name format)]
    (str name "." ext)))

(defn save [data ns name-no-ext format]
  (let [filename (-> (get-filename-ns ns name-no-ext)
                     (add-extension format))]
    (ensure-directory-ns ns)
    (info "saving: " filename)
    (p/save format filename data)
    data ; usable for threading macros  
    ))

(defn loadr [ns name-no-ext format]
  (let [filename (-> (get-filename-ns ns name-no-ext)
                     (add-extension format))]
    (when (.exists (io/as-file filename))
      (p/loadr format filename))))

;; explore

(defn get-ns-list []
  (let [nb-root-dir (io/file (storage-root))]
    (if (and (.exists nb-root-dir)
             (.isDirectory nb-root-dir))
      (->> nb-root-dir
           (.listFiles)
           (map #(.getName %)))
      [])))

(defn get-document-list [ns]
  (let [doc-dir (io/file (str (storage-root) (str ns)))]
    (if (and (.exists doc-dir)
             (.isDirectory doc-dir))
      (->> doc-dir
           (.listFiles)
           (map #(.getName %)))
      [])))

(comment

  (ensure-directory (storage-root))

  (get-filename-ns "demo.study3" "bongo.txt")

  (get-link-ns "demo.notebook.image" "bongo.txt")

  (ensure-directory-ns "demo.test-notebook.apple")

  (save {:a 1 :b "bongotrott" :c [1 2 3]}  "demo.3" "bongotrott" :edn)
  (save  {:a 1 :b "bongotrott" :c [1 2 3]} "demo.3" "bongotrott-1" :edn)
  (save  {:a 1 :b "bongotrott" :c [1 2 3]} "demo.3" "bongotrott-2" :edn)
  (save  {:a 1 :b "bongotrott" :c [1 2 3]} "demo.3" "bongotrott" :bad-format-3)

  ; should fail, needs ds
  (save  {:a 1 :b "bongotrott" :c [1 2 3]} "demo.3" "bongotrott" :arrow)

  (get-ns-list)

  (get-document-list 'demo.3)

  ;(require '[tablecloth.api :as tc])
  ;(save (tc/dataset {:a [1.4 2.5]}) "demo.3" "bongotrott" :nippy)

  ; nippy only works on java11
  ;(save  (tc/dataset {:a [1 2] :b  [1 2] :c [2 3]}) "demo.3" "bongotrott" :arrow)
  ;(save (tc/dataset {:a [1.4 2.5]}) "demo.3" "bongotrott5" :arrow)

  ;(let [ns-nb "demo.3"
  ;      n "ds-daniel"]
  ;  (-> (tc/dataset {:a [1 2 3]
  ;                   :b [4 4 4]
  ;                   ;:c [true false true]
  ;                   })
  ;      (save ns-nb n :nippy)
  ;      (save ns-nb n :csv)
  ;      ;(save ns-nb n :arrow)
  ;      ))

  (-> (loadr "demo.3" "bongotrott-2" :edn)
      :c)

  (loadr "demo.studies.asset-allocation-dynamic" "2" :txt)
  (loadr "demo.studies.asset-allocation-dynamic" "ds2" :bad-format-5)
  (loadr "demo.studies.asset-allocation-dynamic" "ds-777" :nippy)
  (loadr "demo.studies.asset-allocation-dynamic" "ds2" :arrow)

  (delete-recursively "demo/rdocument/demo/notebook/hello")

; 
  )
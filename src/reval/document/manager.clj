(ns reval.document.manager
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [clojure.java.io :as io]
   ; persister
   [reval.persist.protocol :as p]
   [reval.config :refer [storage-root url-root]]))

;; helper fns

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn- ensure-directory-storage-root []
  (ensure-directory (storage-root)))

(defn- make-filename [ns name format]
  (let [;nss *ns*
        ext (clojure.core/name format)
        study-dir (str (storage-root) ns)
        file-name (str study-dir "/" name "." ext)]
    (ensure-directory-storage-root)
    (ensure-directory study-dir)
    file-name))

;; api

(defn get-filename-ns [ns name format]
  (let [ext (str format)
        root (storage-root)
        dir (str root (str ns) "/")]
    (ensure-directory root)
    (ensure-directory dir)
    (str dir name "." ext)))

(defn get-link-ns [ns name ext]
  (str (url-root) (str ns) "/" name "." ext))

(defn save [data ns name format]
  (p/save format (make-filename ns name format) data)
  data ; usable for threading macros
  )

(defn loadr [ns name format]
  (p/loadr format (make-filename ns name format)))

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

  (ensure-directory-storage-root)

  (get-filename-ns "demo.study3" "bongo" "txt")
  (get-link-ns "demo.study3" "bongo" "txt")

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

  (loadr "demo.studies.asset-allocation-dynamic" "2" :text)
  (loadr "demo.studies.asset-allocation-dynamic" "ds2" :bad-format-5)
  (loadr "demo.studies.asset-allocation-dynamic" "ds-777" :nippy)
  (loadr "demo.studies.asset-allocation-dynamic" "ds2" :arrow)

; 
  )
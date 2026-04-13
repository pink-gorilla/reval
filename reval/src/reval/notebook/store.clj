(ns reval.notebook.store
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [ednx.fipp :refer [spit-fipp]]
   [ednx.edn :refer [slurp-edn]]
   [ednx.tick.fipp :refer [add-tick-fipp-printers!]]
   [ednx.tick.edn :refer [add-tick-edn-handlers!]]
   [dali.store :refer [store-data]]
   [dali.store.file :refer [set-sub-path]]
   [reval.namespace.path :refer [ns->dir]]))

(add-tick-fipp-printers!)
(add-tick-edn-handlers!)


; get-filename and get-link may NOT contain the fmt parameter
; the name contains the extension. The reason is, that we have
; a format detector based on full name. This is important, as
; formats that contain 2 extensions (.nippy.gz) might be hard
; to parse via a single regex, and therefore are not so easy
; to use in routing tables. 

; demo/rdocument/demo.notebook.apple/notebook.edn

;; URL side

(defn url-root [this]
  (get-in this [:rdocument :rpath]))

(defn get-link-ns [this ns name]
  (str (url-root this)  ns "/" name))

;; FILE side

(defn fpath [this]
  (get-in this [:rdocument :fpath]))

(defn get-path-ns [this nbns]
  (str (fpath this) "/" (ns->dir nbns)))

(defn get-filename-ns [this nbns name]
  (str (get-path-ns this nbns) "/" name))

(defn delete-recursively [fname]
  (doseq [f (reverse (file-seq (clojure.java.io/file fname)))]
    (clojure.java.io/delete-file f true)))

(defn delete-notebook [this nbns]
  (let [ns-path (get-path-ns this nbns)]
    (debug "deleting notebook " nbns "path: " ns-path)
    (delete-recursively ns-path)))

(defn- add-extension [name format]
  (let [ext (clojure.core/name format)]
    (str name "." ext)))

(defn store-embedded-content [this nb nbns]
  (let [ns-path (ns->dir nbns)
        dali-store (:dali-store this) 
        sub-path (str "/" ns-path "/")
        _ (debug "sub-path: " sub-path)
        _ (set-sub-path dali-store  sub-path)
        store-segment (fn [segment]
                        (if-let [result  (:result segment)]
                          (assoc segment :result (store-data dali-store result))
                          segment))]
    (update nb :content (fn [segments]
                          (->> segments
                               (map store-segment)
                               (into [])
                               )))))


(defn save-notebook [this data nbns]
  (debug "saving.. this: " this)
  (let [path (get-path-ns this nbns)
        _  (fs/create-dirs path)
        filename (-> (get-filename-ns this nbns "notebook")
                     (add-extension :edn))
        data (store-embedded-content this data nbns)]
    (set-sub-path (:dali-store this) "/")
    (spit-fipp filename data)
    data))



(defn load-notebook [this nbns]
  (let [filename (-> (get-filename-ns this nbns "notebook")
                     (add-extension :edn))]
    (when (.exists (io/as-file filename))
      (slurp-edn filename))))

;; explore

(defn get-ns-list [this]
  (let [nb-root-dir (io/file (fpath this))]
    (if (and (.exists nb-root-dir)
             (.isDirectory nb-root-dir))
      (->> nb-root-dir
           (.listFiles)
           (map #(.getName %)))
      [])))

(defn get-document-list [this ns]
  (let [doc-dir (io/file (str (fpath this) (str ns)))]
    (if (and (.exists doc-dir)
             (.isDirectory doc-dir))
      (->> doc-dir
           (.listFiles)
           (map #(.getName %)))
      [])))

(comment

  (require '[reval.config :refer [reval]])
  
  
  (fpath reval)
  ; ".reval/public/rdocument"
  (get-path-ns reval "demo.study")
  ;".reval/public/rdocument/demo/study"
  
  (def this {:config {:rdocument  {:fpath "/tmp/rdocument"
                                   :rpath "/api/rdocument/file"}
                      :collections {:user [:clj "user/notebook/"]
                                    :demo [:clj "demo/notebook/"]
                                    :demo-cljs [:cljs "demo/notebook/"]}}})

  (get-filename-ns this "demo.study3" "bongo.txt")

  (get-link-ns this "demo.notebook.image" "bongo.txt")

  (save-notebook this {:a 1 :b "bongotrott" :c [1 2 3]}  "demo.3" )
  
  

  (get-ns-list this)

  (get-document-list this 'demo.3)

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
  
  (-> (load-notebook this "demo.3")
      :c)
  

  (delete-recursively "demo/rdocument/demo/notebook/hello")

; 
  )
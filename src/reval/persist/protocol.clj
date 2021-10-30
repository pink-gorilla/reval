(ns reval.persist.protocol)

;; internally we use :edn :txt :json
;; this is conveted to filename-extension ".edn" ".txt" ".json"

(defmulti save
  (fn [type _ _] ;file-name data (avoid lint warnings)
    type))

(defmulti loadr
  (fn [type _] ; file-name   (avoid lint warnings)
    type))

#_(def formats
    {; browser can handle those:
     :text {:ext "txt" :save text/save :load text/loadr}
     :edn {:ext "edn" :save edn/save :load edn/loadr}
  ; :png {:ext "png" :save image/save-png}
   ;:csv {:ext "csv" :save tds-persist/save-csv}

   ;:arrow {:ext "arrow" :save tds-persist/save-arrow :loadr tds-persist/load-arrow}
   ; browser cannot handle this
   ;:nippy {:ext "nippy.gz" :save tds-persist/save-nippy :loadr tds-persist/load-nippy}
     })

(defn known-formats []
  (->> (methods save)
       keys
       (remove #(= :default %))
       (into [])))

(defn filename->extension [filename]
  (let [[_ path _ ext] (re-matches #"([\w\/\.]*)\/+([\w-]+)\.([\w\.]+)$" filename) ; with path
        [_ _ ext2] (re-matches #"([\w-]+)\.([\w\.]+)$" filename)] ; no path
    (keyword (if path
               ext
               ext2))))

(defn filename->format [filename]
  (let [ext (filename->extension filename)]
    (when ext
      (str ext))))

(comment

  ; just filename
  (filename->extension "bongo.txt")
  (filename->extension "bongo.csv")
  (filename->extension "ds1.nippy.gz")
  (filename->extension "item-plot.png")

  ; should give nil
  (filename->extension "bongo")
  (filename->extension ".bongo")

  ; filename with path
  (filename->extension "/tmp/notebooks/demo.studies.a/bongo.csv")
  (filename->extension "/tmp/notebooks/demo.studies.a/ds1.nippy.gz")

  (filename->format "bongo.edn")
  (filename->format "/tmp/notebooks/demo.studies.a/ds1.nippy.gz")
  (filename->format "bongo.png")

;
  )
#_(defn filename->encoding [this k]
  ;(debug "filename->encoding: " this)
    (:encoding (split-filename (k this))))

(comment

  ; you need to require reval/default so that side-effects are run first.  
  (known-formats)

;
  )
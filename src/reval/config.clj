(ns reval.config)

(def tmp-config
  {:storage-root "/tmp/rdocument/"
   :url-root "/api/rdocument/file/"})

(defonce config
  (atom tmp-config))

(defn storage-root []
  (:storage-root @config))

(defn url-root []
  (:url-root @config))

(defn set-config! [c]
  (reset! config c))

(defn use-tmp []
  (set-config! tmp-config))

(defn use-project []
  (set-config! {:storage-root "rdocument/"
                :url-root "/api/rdocument/file/"}))

(comment

  (storage-root)
  (url-root)

  (use-project)

 ; 
  )
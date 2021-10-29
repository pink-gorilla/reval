(ns reval.config)

(defonce config
  (atom {:storage-root "/tmp/document/"
         :url-root "/api/viewer/"}))

(defn storage-root []
  (:storage-root @config))

(defn url-root []
  (:url-root @config))

(defn use-tmp []
  (reset! config {:storage-root "/tmp/document/"
                  :url-root "/api/document/"}))

(defn use-project []
  (reset! config {:storage-root "document/"
                  :url-root "/api/document/"}))

(comment

  (storage-root)
  (url-root)

  (use-project)

 ; 
  )
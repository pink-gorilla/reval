(ns reval.config)

(defonce config
  (atom {:storage-root "/tmp/studies/"
         :url-root "/api/viewer/"}))

(defn storage-root []
  (:storage-root @config))

(defn url-root []
  (:url-root @config))

(comment

  (storage-root)
  (url-root)

  (reset! config {:storage-root "document/"
                  :url-root "/api/viewer/"}))
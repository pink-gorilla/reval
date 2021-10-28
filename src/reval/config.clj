(ns reval.config)

(defonce config
  (atom {:storage-root "/tmp/studies/"
         :url-root "/api/viewer/"}))

(defn storage-root []
  (:storage-root @config))

(defn url-root []
  (:url-root @config))

(defn use-tmp []
  (reset! config {:storage-root "/tmp/"
                  :url-root "/api/viewer/"}))

(defn use-project []
  (reset! config {:storage-root "document/"
                  :url-root "/api/viewer/"}))

(comment

  (storage-root)
  (url-root)

  (use-project))
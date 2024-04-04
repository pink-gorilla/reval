(ns reval.config)

(def default-reval-config
  {:rdocument  {:storage-root "/tmp/rdocument/"
                :url-root "/api/rdocument/file/"}
   :collections {:user [:clj "user/notebook/"]
                 :demo [:clj "demo/notebook/"]
                 :demo-cljs [:cljs "demo/notebook/"]
                 }})

(def config (atom default-reval-config))

(defn set-config! [reval-config]
 (println "reval config: " reval-config)
  (let [reval-config {:rdocument (or (:rdocument reval-config)
                                     (:rdocument default-reval-config))
                      :collections (or (:collections reval-config)
                                       (:collections default-reval-config))}]
    (reset! config reval-config)))

(defn get-in-reval-config [path]
  (get-in @config path))

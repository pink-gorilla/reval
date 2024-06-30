(ns reval.core
  (:require
   [taoensso.timbre :as timbre :refer [debug info infof error]]
   [clj-service.core :refer [expose-functions]]
   [reval.default]  ; side effects to include all default converters
   ))

(def default-reval-config
  {:rdocument  {:storage-root "/tmp/rdocument/"
                :url-root "/api/rdocument/file/"}
   :collections {:user [:clj "user/notebook/"]
                 :demo [:clj "demo/notebook/"]
                 :demo-cljs [:cljs "demo/notebook/"]}})

(defn sanitize-config [reval-config]
  {:rdocument (or (:rdocument reval-config)
                  (:rdocument default-reval-config))
   :collections (or (:collections reval-config)
                    (:collections default-reval-config))})

(defn start-reval [{:keys [config clj reval-role] :as this}]
  (info "starting reval service..")
  (let [config (sanitize-config config)]
    (when clj
      (expose-functions clj
                        {:name "reval"
                         :symbols ['reval.viz.eval/viz-eval-blocking
                                   'reval.save/save-code
                                   'reval.document.notebook/load-src]
                         :permission reval-role
                         :fixed-args []})
      (expose-functions clj
                        {:name "reval"
                         :symbols ['reval.document.collection/nb-collections ; this
                                   'reval.document.notebook/load-notebook ; this
                                   'reval.document.notebook/eval-notebook ; this
                                   'reval.document.notebook/save-notebook] ; this
                         :permission reval-role
                         :fixed-args [this]}))
    {:config config}))


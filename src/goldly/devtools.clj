(ns goldly.devtools
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [modular.config :refer [get-in-config]]
   [goldly.service.core :as s]
   [goldly.document-handler] ; side effect
   [reval.document.collection :as nbcol]
   [reval.document.notebook :refer [load-notebook eval-notebook save-notebook]]
   [reval.config :as reval-config]
   [reval.default] ; side effects
   ))

(info "goldly devtools loading..")

(def default-devtools-config
  {:rdocument  {:storage-root "/tmp/rdocument/"
                :url-root "/api/rdocument/file/"}
   :collections {:demo [:clj "demo/notebook/"]}})

(defn get-config []
  (let [user (get-in-config [:devtools])
        user-rdocument (:rdocument user)
        user-collections (:collections user)]
    (if user
      {:rdocument (if user-rdocument
                    user-rdocument
                    (:rdocument default-devtools-config))
       :collections (if user-collections
                      user-collections
                      (:collections default-devtools-config))}
      (do (warn "no :devtools key in goldly-config. using default deftools settings")
          default-devtools-config))))

(def devtools-config
  (get-config))

(reval-config/set-config! (:rdocument devtools-config))

(defn nb-collections []
  (nbcol/get-collections (:collections devtools-config)))

(s/add {:nb/collections nb-collections
        :nb/load  load-notebook
        :nb/eval  eval-notebook
        :nb/save save-notebook})

(comment
  devtools-config
  (nb-collections)

;  
  )
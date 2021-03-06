(ns reval.services
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [modular.config :as config :refer [get-in-config]]
   [reval.type.converter :refer [value->hiccup]]
   [reval.kernel.clj-eval :refer [clj-eval-raw clj-eval]]
   [reval.document.collection :as nbcol]
   [reval.document.notebook :refer [load-src load-notebook eval-notebook save-notebook]]
   [reval.default] ; side effects
   [goldly.service.core :as s]
   [reval.document-handler] ; side effect
   ))

(defn save-code [{:keys [path code]}]
  (info "saving code to: " path)
  (spit path code))

(def default-reval-config
  {:rdocument  {:storage-root "/tmp/rdocument/"
                :url-root "/api/rdocument/file/"}
   :collections {:demo [:clj "demo/notebook/"]}})

(defn viz-eval [{:keys [code ns]}]
  (let [{:keys [err value] :as er}
        ;(clj-eval-raw code)
        (clj-eval {:code code :ns ns})]

    (if err
      er
      (->  er
           (assoc :hiccup (value->hiccup value))
           (dissoc :value)))))

(comment
  (viz-eval {:code "(/ 1 3)"})
  (viz-eval {:code "(/ 1 0)"})
  (clj-eval-raw "(/ 1 0)")

;
  )
(info "reval loading..")

(defn get-config []
  (let [user (get-in-config [:reval])
        user-rdocument (:rdocument user)
        user-collections (:collections user)]
    (if user
      {:rdocument (if user-rdocument
                    user-rdocument
                    (:rdocument default-reval-config))
       :collections (if user-collections
                      user-collections
                      (:collections default-reval-config))}
      (do (warn "no :reval key in config. using default reval settings")
          default-reval-config))))

(config/set! :reval (get-config))

(defn nb-collections []
  (nbcol/get-collections (get-in-config [:reval :collections])))

(s/add {:viz-eval viz-eval
        :nb/collections nb-collections
        ; used in repl:
        :nb/load-src load-src
        :nb/save-code save-code

        :nb/load  load-notebook
        :nb/eval  eval-notebook
        :nb/save save-notebook})

(comment

  (nb-collections)

;  
  )
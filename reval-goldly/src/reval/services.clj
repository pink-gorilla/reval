(ns reval.services
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [modular.config :as config :refer [get-in-config]]
   [reval.viz.data :refer [value->data]]
   [reval.viz.eval :refer [viz-eval]]
   [reval.kernel.clj-eval :refer [clj-eval-raw clj-eval]]
   [reval.document.collection :as nbcol]
   [reval.document.notebook :refer [load-src load-notebook eval-notebook save-notebook]]
   [reval.default] ; side effects
   [goldly.service.core :as s]
   [modular.permission.service :refer [add-permissioned-services]]
   [reval.document-handler] ; side effect
   [scratchpad.core]))

(defn save-code [{:keys [path code]}]
  (info "saving code to: " path)
  (spit path code))

(def default-reval-config
  {:rdocument  {:storage-root "/tmp/rdocument/"
                :url-root "/api/rdocument/file/"}
   :collections {:demo [:clj "demo/notebook/"]}})

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

#_(s/add {'reval.viz.eval/viz-eval viz-eval ; :viz-eval
        'reval.services/nb-collections  nb-collections ;  :nb/collections
        ; used in repl:
        'reval.document.notebook/load-src reval.document.notebook/load-src ;:nb/load-src
        'reval.services/save-code save-code ; :nb/save-code
        'reval.document.notebook/load-notebook reval.document.notebook/load-notebook ; :nb/load
        'reval.document.notebook/eval-notebook reval.document.notebook/eval-notebook ;  :nb/eval
        'reval.document.notebook/save-notebook reval.document.notebook/save-notebook ; :nb/save
        })

(start-services
  {:name "reval"
   :permission #{:dev}
   :symbols ['reval.viz.eval/viz-eval
             'reval.services/nb-collections
             ; used in repl:
             'reval.document.notebook/load-src
             'reval.services/save-code
             'reval.document.notebook/load-notebook
             'reval.document.notebook/eval-notebook
             'reval.document.notebook/save-notebook]})

              

(defn log [x]
  (spit "event.log" (str x \newline) :append true))

(defn to-scratchpad [x]
  (info "sending to scratchpad..")
  (let [viz (value->data x)]
    (scratchpad.core/show! viz)))

;; add log function to tap
; (add-tap log)
(add-tap to-scratchpad)

(comment

  (nb-collections)

;  
  )

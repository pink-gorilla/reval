(ns goldly.devtools
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [webly.config :refer [get-in-config]]
   [goldly.service.core :as s]
   [goldly.document-handler] ; side effect
   [reval.document.collection :as c]
   [reval.document.notebook :refer [load-notebook eval-notebook]]
   [reval.default] ; side effects
   ))

(info "goldly devtools loading..")

; localhost:9100
(spit ".nrepl-port" "9100") ; todo - add this to goldly!


(defn nb-collections []
  (let [devtools (get-in-config [:devtools])
        devtools (if devtools 
                   devtools
                     (do (warn "no :devtools key in goldly-config. using default deftools settings")
                         {:demo [:clj "demo/notebook/"]})
                     )]
  (c/get-collections devtools)))


(s/add {:nb/collections nb-collections
        :nb/load  load-notebook
        :nb/eval  eval-notebook
        :nb/save save-notebook})
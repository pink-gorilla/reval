(ns goldly.devtools
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [reval.document.collection :as c]
   [goldly.service.core :as s]
   ; side effects:
   [reval.default]
   [goldly.document-handler]))

(info "goldly devtools loading..")

; localhost:9100
(spit ".nrepl-port" "9100") ; todo - add this to goldly!

(defn nb-collections []
  (c/get-collections
   {:demo [:clj "demo/notebook/"]
    :user [:clj "demo/notebook_test/"]}))


(s/add {:nb/collections nb-collections })
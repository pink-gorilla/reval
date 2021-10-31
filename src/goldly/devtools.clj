(ns goldly.devtools
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   ; side effects:
   [reval.default]
   [goldly.document-handler]))

(info "goldly devtools loading..")

; localhost:9100
(spit ".nrepl-port" "9100") ; todo - add this to goldly!


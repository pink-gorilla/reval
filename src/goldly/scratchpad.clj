(ns goldly.scratchpad
  (:require
   [taoensso.timbre :refer [info]]
   [webly.ws.core :refer [send-all! send-response connected-uids]]))


(defn clear! []
  (info "clearing scratchpad: ")
  (send-all! [:scratchpad/msg {:op :clear}])
  nil)


(defn show! [h]
  (info "sending to scratchpad: " h)
  (send-all! [:scratchpad/msg {:op :show
                               :hiccup h
                               :ns (str *ns*)}])
  h)


(comment
  (show! [:p "hello, scratchpad!"])
  
  (clear!)
;  
  )
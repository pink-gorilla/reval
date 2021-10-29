(ns goldly.scratchpad
  (:require
   [taoensso.timbre :refer [info]]
   ;[reval.type.protocol :refer [to-hiccup]]
   [reval.type.converter :refer [value->hiccup]]
   [webly.ws.core :refer [send-all! send-response connected-uids]]))

(defn clear! []
  (info "clearing scratchpad: ")
  (send-all! [:scratchpad/msg {:op :clear}])
  nil)


(defn ->hiccup [h]
  (if (vector? h)
    h
    ;(to-hiccup h)
    (value->hiccup h)
    ))

(defn show! [h-or-type]
  (let [h (->hiccup h-or-type)]
    (info "sending to scratchpad: " h)
    (send-all! [:scratchpad/msg {:op :show
                                 :hiccup h
                                 :ns (str *ns*)}])
    h))

(comment
  (show! [:p "hello, scratchpad!"])

  (clear!)
;  
  )
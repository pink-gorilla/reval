(ns reval.goldly.page.eval-remote
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [modular.ws.core :as ws]
   [goldly.sci :refer [compile-sci-async compile-sci]]
   [goldly.service.core :as service]))

; {:op     :show :clear
;  :hiccup [:p "hi"]
;  :ns     demo.playground.cljplot

(defn remote-eval [code]
  ;(println "remote eval: " code)
  (let [eval-result (compile-sci code)]
     ;(rf/dispatch [:goldly/send :scratchpad/evalresult {:code code :result eval-result}])
     ;(run-cb {:fun :scratchpad/evalresult :args {:code code :result eval-result}})
    (ws/send! [:scratchpad/evalresult {:code code :result eval-result}] (fn [& _]) 2000)))

(defn process-repl-op [{:keys [op _hiccup code] :as _msg}]
  (case op
    ;:clear (clear-scratchpad)
    ;:show  (show-hiccup hiccup)
    :eval (remote-eval code)
    ;(println "unknown op:" op)
    ))

(rf/reg-event-fx
 :repl/msg
 (fn [{:keys [_db]} [_ msg]]
   ;(println "repl msg received: " msg)
   (process-repl-op msg)
   nil))


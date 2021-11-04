


(defn rdoc-link [ns name]
  (str "/api/rdocument/file/" ns "/" name))

(defn block [& children]
  (into [:div.bg-blue-400.m-5.inline-block {:class "w-1/4"}]
        children))

;; websocket helper

(defn print-result [t]
  (fn [r]
    (println "callback result: " r)))

(defn send-msg [{:keys [type args fn-callback timeout]
                 :or {args []
                      fn-callback (print-result type)
                      timeout 5000}}]
  (rf/dispatch [:ws/send [type args] fn-callback timeout]))




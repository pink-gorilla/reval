(ns reval.kernel.clj
  (:require
   [clojure.core :refer [read-string load-string]]
   [clojure.core.async :refer [>! chan close! go <! <!!]]
   [taoensso.timbre :as timbre :refer [debugf info error]]
   [reval.helper.id :refer [guuid]]
   [reval.kernel.protocol :refer [kernel-eval]]))

(defmacro with-out-str-data-map
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*out* s#]
       (let [r# ~@body]
         {:result r#
          :out    (str s#)}))))

(defn clj-eval-sync [id code]
  (let [m {:src code
           :id id}]
    (try
      (let [eval-result  (with-out-str-data-map (load-string code))]
        (merge m eval-result))
      (catch Exception e
        (merge m {:error e})))))

(comment
  (clj-eval-sync 3 "(println 3) (+ 3 4)")
 ; 
  )
(defmethod kernel-eval :clj [{:keys [id code]
                              :or {id (guuid)}}]
  ; no logging in here. 
  ; when capturing eval result, it is not a good idea.
  (let [c (chan)]
    (go
      (>! c (clj-eval-sync id code))
      (close! c))
    c))

(comment

  (clj-eval-sync "(+ 3 4)\n5\n{:a 3}888")

; 
  )
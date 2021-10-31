(ns reval.kernel.clj-eval
  (:require
   [clojure.core :refer [read-string load-string]]
   [clojure.core.async :refer [>! close! go <! <!! chan]]
   [taoensso.timbre :as timbre :refer [debugf info error]]
   [reval.helper.id :refer [guuid]]
   [reval.kernel.protocol :refer [kernel-eval]]))

(defmacro with-out-str-data-map
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*out* s#]
       (let [r# ~@body]
         {:value r#
          :out    (str s#)} ; nrepl compatible!
         ))))

(defn- clj-eval-raw [code]
  (try
    (with-out-str-data-map (load-string code))
    (catch Exception e
      {:err e})))

(defn clj-eval
  "evaluate code in namespace ns
   if no ns is passed, it will execute the code in the current ns (*ns*)
   returns the result of the last form in code.
   (it would be very easy to return all results as well)"
  [{:keys [id code ns]
    :or {id (guuid)
         ns (str *ns*)}}]
  (let [ns-s (if (string? ns)
               (symbol ns)
               ns)
        er (clj-eval-raw (str "(ns " ns-s " ) "
                              "[ [" code "] (str *ns*)" "]"))
        {:keys [value]} er
        [value-new ns-after] value]
    (merge er
           {:code code
            :id id
            :ns ns-after
            :value (last value-new)})))

(defmethod kernel-eval :clj [seg]
  ; no logging in here. 
  ; when capturing eval result, it is not a good idea.
  (let [c (chan)]
    (go
      (>! c (clj-eval seg))
      (close! c))
    c))

(comment
  (read-string "(+ 1 2) (- 3 2)") ; reads next expression from string
  (load-string "(ns bongo)")
  (load-string "*ns*")

  (-> *ns* class)
  (-> *ns* type)
  (in-ns 'bongo)

  (clj-eval-raw "(+ 3 4)\n5\n{:a 3}888")
  (clj-eval-raw "(ns willy) (def a 3) (println 55) (str *ns*)")
  (clj-eval-raw "1 2 3")

  (clj-eval {:code "(println 3) (def x 777) (+ 3 4)" :ns "bongo"})
  (clj-eval {:code "x" :ns "bongo" :id 3})

  (clj-eval {:code "(ns bongo (:require [reval.config :as c]))" :ns "bongo"})
  (clj-eval {:code "(ns bongo (:require [reval.config :as c]))" :ns "bongo"})
  (clj-eval {:code "(c/use-project)"  :ns "bongo"})
  (clj-eval {:code "*ns*"  :ns "bongo"})

  (let [c (kernel-eval {:code "(ns bongo) (println 3) (+ 5 5)" :kernel :clj})]
    (<!! c))

; 
  )
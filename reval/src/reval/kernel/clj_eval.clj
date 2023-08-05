(ns reval.kernel.clj-eval
  (:require
   [clojure.string :as string]
   [taoensso.timbre :refer [debug info warnf error]]
   [clojure.core :refer [read-string load-string]]
   [clojure.core.async :refer [>! close! go <! <!! chan]]
   [modular.helper.id :refer [guuid]]
   [reval.kernel.protocol :refer [kernel-eval]]))

(defn stack-frame
  "Return a map describing the stack frame."
  [^StackTraceElement frame]
  {:name   (str (.getClassName frame) "/" (.getMethodName frame))
   :file   (.getFileName frame)
   :line   (.getLineNumber frame)
   :class  (.getClassName frame)
   :method (.getMethodName frame)})

(defn stacktrace [e]
  (->> e
       .getStackTrace
       (map stack-frame)
       (into [])))

(defn err [e]
  {:class (.getName (class e))
   :message (.getMessage e)
   :stacktrace (stacktrace e)})

(defmacro with-out-str-data-map
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*out* s#]
       (let [r# ~@body]
         {:value r#
          :out    (str s#)} ; nrepl compatible!
         ))))

(defn clj-eval-raw [code]
  (try
    (with-out-str-data-map (load-string code))
    (catch Exception e
      {:err (err (.getCause e))})))

(load-string "(+ 4 4) (* 4 4)")
(try (load-string "(/ 4 0)")
     (catch Exception e
      ;(println "ex: " e)
      ;(stacktrace e)
      ;(.getMessage e)
       (.getCause e)
       (class e)
       (class (.getCause e))

     ; type
       ))
(clj-eval-raw "(/ 4 0)")

(defn clj-eval
  "evaluate code in namespace ns
   if no ns is passed, it will execute the code in the current ns (*ns*)
   returns the result of the last form in code.
   (it would be very easy to return all results as well)"
  [{:keys [id code ns]
    :or {id (guuid)}}]
  (let [code-ns (if (and ns (not (string/blank? ns)))
                  (str "(ns " ns " ) ")
                  "nil")
       ; _ (error "code-to-set-ns: " code-ns)
        code-with-ns (str code-ns " [ " code " (str *ns*) ]")
       ; _ (error "full code: " code-with-ns)
        er-code (clj-eval-raw code-with-ns)
       ; _ (error "er-code: " er-code)
        ; [[nil] "notebook.study.movies"]
        {:keys [value]} er-code
        ns-after (if (:err er-code)
                   ns  ; on compile exception the ns does not change
                   (last value))
        values-new (drop-last value)
        last-value (last values-new)
        ;er-code (clj-eval-raw code)
        ;er-ns-after (clj-eval-raw "*ns*")
        ;ns-after  (-> er-ns-after :value str) ; (str *ns*)
        r (merge er-code
                 {:id id
                  :code code
                  :ns ns-after}
                 {:value last-value
                  :code code})]

    ;(info "eval-result: " r)
    r))

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

  (->> (clj-eval-raw "(+ 3 4")
       :err
      ;type
      ;class
       ;stacktrace
       ;.getCause
       ;.getVia
       )
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

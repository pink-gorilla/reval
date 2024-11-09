(ns reval.dali.plot.stacktrace)

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

; (err (.getCause e))
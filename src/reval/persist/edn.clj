(ns reval.persist.edn
  (:require
   [taoensso.timbre :refer [debug info warnf]]
   [time-literals.data-readers] ;; For literals
   [time-literals.read-write] ;; For printing/writing
   [fipp.clojure]
   [fipp.ednize]
   [clojure.edn]
   [reval.helper.date :refer [now-str]]
   [reval.persist.protocol :refer [save loadr]])
  (:import (java.io StringWriter)))

; fast, but no pretty-print (makes it difficult to detect bugs)
#_(defn write [filename data]
    (spit filename  (pr-str data)))

; redirect std out is NOT a good idea in an edn writer
; because every console output will be printed to the edn doucment
(defn pprint [data opts]
  (with-out-str
    (fipp.clojure/pprint data opts)))

(defn pprint-str [data]
  (let [sw (StringWriter.)]
    (fipp.clojure/pprint data {:width 60 :writer sw :print-meta true})
    (str sw)))

(extend-protocol fipp.ednize/IEdn
  java.time.LocalDate
  (fipp.ednize/-edn [x]
    (tagged-literal 'time/date (str x)))
  java.time.LocalDateTime
  (fipp.ednize/-edn [x]
    (tagged-literal 'time/date-time (str x))))

(defmethod save  :edn [_ file-name data]
  (info "saving edn file: " file-name)
  (let [comment (str "; saved on "
                     (now-str)
                     "\r\n")
        sedn (pprint-str data)
        s (str comment sedn)]
    (spit file-name s)
    data  ; important to be here, as save-study is used often in a threading macro
    ))

(defn read-str [s]
  (clojure.edn/read-string
   {:readers time-literals.read-write/tags} s))

(defmethod loadr :edn [_ file-name]
  (info "loading edn file: " file-name)
  (-> (slurp file-name)
      (read-str)))

(comment
  (save "/tmp/test3.edn" {:a 1 :b [1 3 4]})
  (-> (loadr "/tmp/test3.edn")
      :b)

  (loadr "document/notebook.image/notebook.edn")
  (loadr "document/notebook.apple/notebook.edn")

  (time-literals.read-write/print-time-literals-clj!)

  (clojure.edn/read-string "#inst \"1985-04-12T23:20:50.52Z\"")
  (clojure.edn/read-string  "#time/date \"2021-11-04\"")


  (def x (read-str "#time/date \"2011-01-01\""))
  (def x (read-str "#time/date-time \"2021-11-04T00:52:59.694154533\""))

  x
  (class x)

  (pprint-str x)

  (str x)
  (pr-str x)
  (type x)

  (tagged-literal 'time/date (str x))

  (fipp.clojure/pprint x)

;  
  )
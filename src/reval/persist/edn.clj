(ns reval.persist.edn
  (:require
   [taoensso.timbre :refer [debug info warnf]]
   [fipp.clojure]
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

(defmethod save  :edn [_ file-name data]
  (info "saving edn file: " file-name)
  (let [sw (StringWriter.)
        comment (str "; saved on "
                     (now-str)
                     "\r\n")
        _ (fipp.clojure/pprint data {:width 60 :writer sw})
        sedn (str sw)
        s (str comment sedn)]
    (spit file-name s)
    data  ; important to be here, as save-study is used often in a threading macro
    ))

(defmethod loadr :edn [_ file-name]
  (info "loading edn file: " file-name)
  (-> (slurp file-name)
      (clojure.edn/read-string)))

(comment
  (save "/tmp/test3.edn" {:a 1 :b [1 3 4]})
  (-> (loadr "/tmp/test3.edn")
      :b)

  (loadr "document/notebook.image/notebook.edn")
  (loadr "document/notebook.apple/notebook.edn")

;  
  )
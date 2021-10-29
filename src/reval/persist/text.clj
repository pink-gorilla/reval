(ns reval.persist.text
  (:require
   [taoensso.timbre :refer [info]]
   [reval.persist.protocol :refer [save loadr]]))

(defmethod save  :txt [_ file-name data]
  (info "saving text file: " file-name)
  (spit file-name (str data))
  data  ; important to be here, as save-study is used often in a threading macro
  )

(defmethod loadr :txt [_ file-name]
  (info "loading text file: " file-name)
  (slurp file-name))

(comment
  (save "/tmp/test.edn" {:a 1 :b [1 3 4]})
  (loadr "/tmp/test.edn")

;  
  )
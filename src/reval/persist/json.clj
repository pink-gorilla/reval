(ns reval.persist.json
  (:refer-clojure :exclude [read])
  (:require
   [clojure.java.io :as io]
   [cheshire.core :as cheshire]
   [reval.persist.protocol :refer [save loadr]]))

(defmethod save :json [_ file-name data]
  (let [my-pretty-printer (cheshire/create-pretty-printer
                           (assoc cheshire/default-pretty-print-options
                                  :indent-arrays? true))]
    (spit file-name (cheshire/generate-string data {:pretty my-pretty-printer}))))

(defmethod loadr :json
  [_ file-name]
  (-> (slurp file-name)
      (cheshire/parse-string true)))




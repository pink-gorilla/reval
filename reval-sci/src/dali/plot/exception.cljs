(ns dali.plot.exception
  (:require
   [dali.spec :refer [create-dali-spec]]))



(defn exception
  "returns a plot specification {:render-fn :spec :data}. 
   The ui shows the exception."
  [text ex]
  (create-dali-spec
   {:viewer-fn 'dali.viewer.text/text-exception
    :data  ;(err (.getCause ex))
    (str text "\r\n"
         (pr-str ex))}))
(ns reval.persist.unknown
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [reval.persist.protocol :refer [save loadr]]))

(defmethod save :default [t]
  (error "unknown format: " t))

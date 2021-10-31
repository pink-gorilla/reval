(ns reval.persist.unknown
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [reval.persist.protocol :refer [save loadr]]))

(defmethod save :default [t filename _]
  (error "unknown format: " t " file: " filename))

(ns reval.helper.id
  #?(:cljs
     (:require
      [cljs-uuid-utils.core :as uuid-cljs])))

(defn guuid []
  #?(:clj (-> (.toString (java.util.UUID/randomUUID)) keyword)
     :cljs  (-> (uuid-cljs/make-random-uuid) uuid-cljs/uuid-string keyword)))

(defn guuid-str []
  #?(:clj (-> (.toString (java.util.UUID/randomUUID)))
     :cljs  (-> (uuid-cljs/make-random-uuid) uuid-cljs/uuid-string)))

(comment
  (guuid)
  (guuid-str))

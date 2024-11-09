(ns reval.helper.http
  (:require
   [taoensso.timbre :as timbre :refer-macros [trace debug debugf info warn error]]
   [clojure.edn :as edn]
   [promesa.core :as p]
   [ajax.core :refer [GET]]))

(defn parse-edn [s]
  (if (string? s)
    (edn/read-string s)
    s))

(defn http-get-edn->a [a url]
  (let [rp (GET url)]
    (-> rp
        (p/then (fn [data]
                  (reset! a (parse-edn data))))
        (p/catch (fn [err]
                   (error "could not load url: " url " error: " err))))))


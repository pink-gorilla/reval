(ns reval.dali.viewer.collection-viewer
  (:require
   [promesa.core :as p]
   [dali.transform.load :refer [load-edn]]
   [webly.spa.mode :refer [get-resource-path]]
   [dali.viewer :refer [viewer2]]))


(defn url-collections []
  ; target/webly/public/rdocument/notebooks.edn
  (str (get-resource-path) "rdocument/notebooks.edn"))


(defn load-transform-edn [{:keys [url link] :as opts}]
  (-> (load-edn opts)
      (p/then (fn [data]
                {:link link
                 :data data}))))


(defn collection-viewer [{:keys [link]}]
  [viewer2
   {:viewer-fn 'reval.dali.viewer.collection/notebook-collection
    :transform-fn  'reval.dali.viewer.collection-viewer/load-transform-edn
    :data {:link link
           :url (url-collections)}}])

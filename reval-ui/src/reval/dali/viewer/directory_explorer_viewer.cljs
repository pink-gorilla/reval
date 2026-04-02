(ns reval.dali.viewer.directory-explorer-viewer
  (:require
   [promesa.core :as p]
   [dali.transform.load :refer [load-edn]]
   [dali.viewer :refer [viewer2]]
   [shadowx.core :refer [get-resource-path]]))

(defn url-namespace-explorer []
  (str (get-resource-path) "rdocument/namespace-explorer.edn"))

(defn load-explorer-edn [{:keys [url link active-res-path]}]
  (-> (load-edn {:url url})
      (p/then (fn [data]
                {:link link
                 :active-res-path active-res-path
                 :data data}))))

(defn directory-explorer-viewer [{:keys [link active-res-path]}]
  [viewer2
   {:viewer-fn 'reval.dali.viewer.directory-explorer/explorer-roots
    :transform-fn 'reval.dali.viewer.directory-explorer-viewer/load-explorer-edn
    :data {:link link
           :active-res-path active-res-path
           :url (url-namespace-explorer)}}])

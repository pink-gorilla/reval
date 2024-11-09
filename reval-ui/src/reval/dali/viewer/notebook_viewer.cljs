(ns reval.dali.viewer.notebook-viewer
  (:require
   [webly.spa.mode :refer [get-resource-path]]
   [reval.document.path :refer [ns->dir]]
   [dali.viewer :refer [viewer2]]))

(defn url-notebook [nbns]
  ; target/webly/public/rdocument/notebook/study/movies/notebook.edn
  ; http://localhost:8080/r/rdocument/notebook/study/movies/notebook.edn
  (if nbns
    (str (get-resource-path) "rdocument/" (ns->dir nbns) "/notebook.edn")
    (str (get-resource-path) "rdocument/welcome.edn")))

(defn notebook-viewer [{:keys [nbns]}]
  [viewer2
   {:viewer-fn 'reval.dali.viewer.notebook/notebook
    :transform-fn 'dali.transform.load/load-edn
    :data {:url (url-notebook nbns)}}])

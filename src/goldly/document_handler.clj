(ns goldly.document-handler
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [ring.util.response :as res :refer [not-found file-response resource-response response]]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.not-modified :refer [wrap-not-modified]]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]]
   [reval.persist.protocol :as p]
   [reval.document.manager :as dm]))

(defn ns-file-handler
  "ring handler to serve files in reproduceable ns folder
   it needs to be added to the routing-table
   parameter: ns + filename"
  [req]
  (let [params (:params req)
        {:keys [ns name]} params]
    (info "nb resource file handler: nbns:" ns "name:" name)
    (if-let [fmt (p/filename->format name)]
      (if-let [file-name (dm/get-filename-ns ns name)]
        (file-response file-name)
        (do (error "viewer filename cannot be created: " ns name)
            (not-found {:body (str "filename cannot be created: " ns name)})))
      (do (error (str "viewer file resource - format could not be determined for name: [" name "]"))
          (not-found {:error (str "format could not be determined: " name)})))))

(def wrapped-ns-file-handler
  (-> ns-file-handler
      (wrap-content-type) ; options
      (wrap-not-modified)))

(add-ring-handler :rdocument/file wrapped-ns-file-handler)

;; rest

(defn get-ns-list
  "ring handler for rest endpoint 
   returns namespaces that have reproduceable documents."
  [req]
  (let [ns-list (dm/get-ns-list)]
    (debug "notebook list user: " ns-list)
    (response {:data ns-list})))

(defn get-ns-files
  "ring handler for rest endpoint 
   returns document names in reproduceable document namespace.
   parameter: ns"
  [req]
  (let [params (:params req)
        {:keys [ns]} params
        filename-list (dm/get-document-list ns)]
    (debug "resources for notebook " ns ": " filename-list)
    (response {:data filename-list})))

(add-ring-handler :rdocument/ns (wrap-api-handler get-ns-list))
(add-ring-handler :rdocument/files (wrap-api-handler get-ns-files))

(comment

  ; (loadr "demo.studies.asset-allocation-dynamic" "2" :text)

  (notebook-resource-file-handler
   {:params {:nbns "demo.studies.asset-allocation-dynamic"
             :name "1.edn"}})

  (notebook-resource-file-handler
   {:params {:nbns "demo.studies.asset-allocation-dynamic"
             :name "2.txt"}})

  (get-resource-list "ta.notebook.persist")

;  
  )
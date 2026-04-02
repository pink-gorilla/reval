(ns reval.core
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [babashka.fs :as fs]
   [dali.store.file :refer [create-dali-file-store]]
   [reval.document.collection :as nbcol]
   [reval.document.explore :as nexplore]
   [reval.default]  ; side effects to include all default converters
   ))

(def nb-welcome
  {:meta {:ns "welcome"}
   :content
   [{:code "(println \"Welcome to Notebook Viewer \")"
     :result ^{:dali true}
     {:viewer-fn 'dali.viewer.hiccup/hiccup
      :data  [:h1.text-blue-800 "Welcome to Notebook Viewer!"]}
     :out "Welcome to Notebook Viewer"}]})

(defn- save-welcome [{:keys [rdocument]}]
  (info "generate index notebook.. ")
  (fs/create-dirs (:fpath rdocument))
  (spit
   (str (:fpath rdocument) "/welcome.edn")
   nb-welcome))

(defn build-notebook-index [{:keys [rdocument collections namespace-root] :as _this}]
  (info "build collection index for collections:  "  collections)
  (let [roots (or namespace-root ["user" "demo"])]
    (fs/create-dirs (:fpath rdocument))
    (spit
     (str (:fpath rdocument) "/notebooks.edn")
     (nbcol/collections-ns-summary collections))
    (spit
     (str (:fpath rdocument) "/namespace-explorer.edn")
     (binding [*print-length* nil *print-level* nil]
       (pr-str (nexplore/namespace-explorer-edn roots))))))

(defn start-reval [{:keys [rdocument collections namespace-root]
                    :or {rdocument {:fpath ".reval/public/rdocument"
                                    :rpath "/r/rdocument"
                                    :url-root "/api/rdocument/file/"}
                         collections {:user [:clj "user/notebook/"]
                                      :demo [:clj "demo/notebook/"]
                                      ;:demo-cljs [:cljs "demo/notebook/"]
                                      }
                         namespace-root ["user" "demo"]}}]
  (info "starting reval service..")
  (let [this {:rdocument rdocument
              :collections collections
              :namespace-root namespace-root
              :dali-store (create-dali-file-store rdocument)}]
    (info "setting reval *env*..")
    (def ^:dynamic *env* this)
    ;(set! *env* this) ; this would not work. dynamic-vars are thread-local
    (build-notebook-index this)
    (save-welcome this)
    this))

(defn eval-collections [{:keys [rdocument collections] :as this}]
  (info "eval collections.. ")
  (nbcol/eval-collections this collections)
  (build-notebook-index this)
  (save-welcome this))


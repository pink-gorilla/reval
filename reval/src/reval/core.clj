(ns reval.core
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [clj-service.core :refer [expose-functions]]
   [dali.store.file :refer [create-dali-file-store]]
   [reval.document.collection :as nbcol]
   [reval.default]  ; side effects to include all default converters
   ))

(defn start-reval [{:keys [clj reval-role rdocument collections]
                    :or {rdocument {:fpath ".reval/public/rdocument"
                                    :rpath "/r/rdocument"
                                    :url-root "/api/rdocument/file/"}
                         collections {:user [:clj "user/notebook/"]
                                      :demo [:clj "demo/notebook/"]
                                      :demo-cljs [:cljs "demo/notebook/"]}}}]
  (info "starting reval service..")
  (let [env {:rdocument rdocument
             :collections collections
             :dali-store (create-dali-file-store rdocument)}]
    (info "setting reval *env*..")
    (def ^:dynamic *env* env)
    ;(set! *env* env) ; this would not work. dynamic-vars are thread-local
    (when clj
      (info "exposing reval clj fns..")
      (expose-functions clj
                        {:name "reval"
                         :symbols ['reval.save/save-code
                                   'reval.document.notebook/load-src]
                         :permission reval-role
                         :fixed-args []})
      (expose-functions clj
                        {:name "reval"
                         :symbols [; all this fns get as first arg env
                                   'reval.document.collection/nb-collections
                                   'reval.document.notebook/load-notebook
                                   'reval.document.notebook/eval-notebook
                                   'reval.document.notebook/save-notebook
                                   'reval.dali.eval/dali-eval-blocking]
                         :permission reval-role
                         :fixed-args [env]}))
    env))

(def nb-welcome
  {:meta {:ns "welcome"}
   :content
   [{:code "(println \"Welcome to Notebook Viewer \")"
     :result ^{:dali true}
     {:viewer-fn 'dali.viewer.hiccup/hiccup
      :data  [:h1.text-blue-800 "Welcome to Notebook Viewer!"]}
     :out "Welcome to Notebook Viewer"}]})

(defn- save-welcome [{:keys [rdocument]}]
  (spit
   (str (:fpath rdocument) "/welcome.edn")
   nb-welcome))

(defn eval-collections [{:keys [rdocument collections] :as this}]
  (info "eval collections.. ")
  (nbcol/eval-collections this collections)
  (info "build collection index for collections:  "  collections)
  (spit
   (str (:fpath rdocument) "/notebooks.edn")
   (nbcol/collections-ns-summary collections))
  (info "generate index notebook.. ")
  (save-welcome this))


(ns demo.viewer
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [debug info warnf]]
   [reval.document.persist :as p]
   [reval.notebook.src-parser :refer [text->notebook]]
   [reval.kernel.clj-eval :refer [clj-eval-sync]]
   [reval.config :as c]
   [reval.helper.id :refer [guuid]]))

(defn document? [doc]
  (and (map? doc)
       (:ns doc)
       (:name doc) ; "notebook" or "eval-results" (auto gen resources are another thing)
       (:content doc)))

(defn eval-result? [er]
  (and (map? er)
       (:src er)
       (:out er)
       (:error er)
       (:result er)))

(defn notebook? [nb]
  (seq? nb))

;(defn notebook-document)


(defn src-view [src]
  [:p src])

(defn console-output [console-output]
  [:p (console-output)])


(defn eval-result-view [{:keys [src out] :as eval-result}]
  [:div {:class "segment"}
   [:p.eval-result-raw (pr-str eval-result)]
   [src-view src]
   [console-output out]])

(reset! c/document-root-dir "documents/")


(defn run-notebook [ns]
  (execute-ns ns eval-result-view))

(comment
  (symbol demo.notebook)

  (run-notebook "notebook.apple")
  (run-notebook "notebook.banana")


 ; 
  )
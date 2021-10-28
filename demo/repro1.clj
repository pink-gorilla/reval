(ns demo.repro1
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [debug info warnf]]
   [reval.document.persist :as p]
   [reval.notebook.src-parser :refer [text->notebook]]
   [reval.kernel.clj :refer [clj-eval-sync]]
   [reval.config :as c]
    [reval.helper.id :refer [guuid]]
   ))

(defn find-filename [ns]
  (-> (str/replace ns #"\." "/")
      (str ".clj")))

(defn load-src [ns]
  (-> ns
      find-filename
      slurp))

(defn src->src-list [src]
  (->>
   (text->notebook :clj src)
   :segments
   (filter #(= (:type %) :code))
   (map #(get-in % [:data :code]))))

(comment 

  (find-filename "notebook.banana")  
  (-> (load-src "notebook.banana")
     type)
  
"(+ 1 1)
   (println 123)
   [1 2 3]
   {:a 3}
   "  
   (-> "notebook.banana"
      load-src
      src->src-list)
  
#_["(+ 1 1)"
   "(println 123)"
   "[1 2 3]"
   "{:a 3}"]


  ;
  )

(defn eval-src [src]
  (clj-eval-sync (guuid) src))

(comment 
  (eval-src "(+ 4 4)")
  
  ;
  )


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


(defn load-document [ns name]
  (p/loadr ns name :edn))


(defn save-document [ns doc]
  (info "saving " ns ":" (:name doc))
  (p/save doc ns (:name doc) :edn))


(defn eval-ns
  "evaluates a clj namespace.
   returns seq of eval-result"
  [ns]
  (let [src (load-src ns)
        src-list (src->src-list src)]
    (map eval-src src-list)))

(defn execute-ns [ns eval-result-view-fn]
  (let [eval-results (eval-ns ns)
        content (if eval-result-view-fn
                  (map eval-result-view-fn eval-results) ; use here a better thing.
                  eval-results)
        doc {:ns ns
             :name "notebook"
             :content content}]
    (save-document ns doc)))


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
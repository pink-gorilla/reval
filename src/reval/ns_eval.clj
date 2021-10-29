(ns reval.ns-eval
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [debug info warnf]]
   [reval.document.manager :as p]
   [reval.notebook.src-parser :refer [text->notebook]]
   [reval.kernel.clj :refer [clj-eval-sync]]
   [reval.helper.id :refer [guuid]]
   [reval.helper.date :refer [now-str]]
   [reval.config :as c]
   [reval.type.converter :refer [value->hiccup]]
   [reval.default]  ; side effects to include all default converters
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

(defn eval-src [ns src]
  (clj-eval-sync (guuid) src ns))

(defn eval-ns-raw
  "evaluates a clj namespace.
     returns seq of eval-result"
  [ns]
  (let [src (load-src ns)
        src-list (src->src-list src)]
    (map (partial eval-src ns) src-list)))

;; document

(defn load-document [ns name]
  (p/loadr ns name :edn))

(defn save-document [ns doc]
  (info "saving " ns ":" (:name doc))
  (p/save doc ns (:name doc) :edn))

(defn document-meta []
  {:java (-> (System/getProperties) (get "java.version"))
   :clojure (clojure-version)
   :eval-finished (now-str)})

(defn eval-result->hiccup [{:keys [value] :as eval-result}]
  (when-let [hiccup (value->hiccup value)]
    (-> eval-result
        (assoc :hiccup hiccup)
        (dissoc :value))))

(defn eval-ns
  ([ns]
   (eval-ns ns eval-result->hiccup)) ; default converter
  ([ns eval-result-view-fn]
   (let [prior-ns (-> *ns* str)
         eval-results (eval-ns-raw ns)
         content (if eval-result-view-fn
                   (map eval-result-view-fn eval-results) ; use here a better thing.
                   eval-results)
         doc {:ns ns
              :name "notebook"
              :meta (document-meta)
              :content (into [] content)}]
     (save-document ns doc)
     (println "restoring prior ns: " prior-ns)
     (eval-src "user " (str "(ns " prior-ns ")"))
     doc)))

(comment

  (find-filename "notebook.banana")
  (-> (load-src "notebook.banana")
      type)

  ;;  "(+ 1 1)
  ;; (println 123)
  ;; [1 2 3]
  ;; {:a 3}"

  (-> "notebook.banana"
      load-src
      src->src-list)

  ;; ["(+ 1 1)"
  ;;  "(println 123)"
  ;;  "[1 2 3]"
  ;;  "{:a 3}"]

  (eval-src "bongo" "(+ 4 4)")
  ;; {:src "(+ 4 4)", 
  ;;  :result 8       note: result has not been processed in any way. result is a single eval result. 
  ;;  :out "", 
  ;;  :id :87929fe8-4003-4c25-9df4-512391857d07 }

  (document-meta)

  (eval-ns "notebook.apple")

  (eval-ns "notebook.image")

;
  )
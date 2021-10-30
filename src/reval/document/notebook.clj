(ns reval.document.notebook
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer [debug info warnf]]
   [reval.document.manager :as p]
   [reval.kernel.clj :refer [clj-eval-sync]]
   [reval.helper.id :refer [guuid]]
   [reval.helper.date :refer [now-str]]
   [reval.config :as c]
   [reval.type.converter :refer [value->hiccup]]
   [reval.default]  ; side effects to include all default converters
   [reval.document.src-parser :refer [text->notebook]]))

;; create

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

(defn src-list->notebook [src-list]
  {:content (->> (map (fn [src]
                        {:src src}) src-list)
                 (into []))})

(defn create-notebook [ns]
  (-> ns
      load-src
      src->src-list
      src-list->notebook
      (assoc :meta {:id (guuid)
                    :created (now-str)
                    :ns ns})))

;; persistence

(defn load-notebook [ns]
  (let [nb (p/loadr ns "notebook" :edn)]
    (if nb
      nb
      (create-notebook ns))))

(defn save-notebook [ns nb]
  (info "saving notebook: " ns)
  (p/save nb ns "notebook" :edn))

; eval

(defn eval-result->hiccup [{:keys [value] :as eval-result}]
  (when-let [hiccup (value->hiccup value)]
    (-> eval-result
        (assoc :hiccup hiccup)
        (dissoc :value))))

(defn eval-src [ns src]
  (clj-eval-sync (guuid) src ns))

(defn eval-ns-raw
  "evaluates a clj namespace.
     returns seq of eval-result"
  [ns]
  (let [src (load-src ns)
        src-list (src->src-list src)]
    (map (partial eval-src ns) src-list)))

(defn eval-notebook
  ([ns]
   (eval-notebook ns eval-result->hiccup)) ; default converter
  ([ns eval-result-view-fn]
   (let [prior-ns (-> *ns* str)
         nb (create-notebook ns)
         eval-results (map #(eval-src ns %) (:content nb))
         content (if eval-result-view-fn
                   (map eval-result-view-fn eval-results) ; use here a better thing.
                   eval-results)
         nb (-> nb
                (assoc :content (into [] content))
                (assoc-in [:meta :eval-finished] (now-str))
                (assoc-in [:meta :java] (-> (System/getProperties) (get "java.version")))
                (assoc-in [:meta :clojure] (clojure-version)))]
     (save-notebook ns nb)
     (println "restoring prior ns: " prior-ns)
     (eval-src "user" (str "(ns " prior-ns ")"))
     nb)))

(comment

  (find-filename "notebook.banana")
  (-> (load-src "notebook.banana")
      type)

  ;;  "(+ 1 1)
  ;; (println 123)
  ;; [1 2 3]
  ;; {:a 3}"

  (-> "demo.notebook.banana"
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

  (create-notebook "demo.notebook.apple")
  (eval-notebook "demo.notebook.apple")

  (eval-ns "notebook.image")

;
  )
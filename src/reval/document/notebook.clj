(ns reval.document.notebook
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [taoensso.timbre :refer [debug info warnf]]
   [modular.helper.id :refer [guuid]]
   [modular.helper.date :refer [now-str]]
   [reval.type.converter :refer [value->hiccup]]
   [reval.config :as c] ; this really is the reproduceabe document config
   [reval.document.manager :as rdm]
   [reval.document.classpath :refer [ns->dir]]
   [reval.document.src-parser :refer [text->notebook]]
   [reval.kernel.clj-eval :refer [clj-eval]]
   [reval.default]  ; side effects to include all default converters
   ))

;; create

(defn ns->filename [ns fmt]
  (let [name (ns->dir ns)]
    (case fmt
      :clj (str name ".clj")
      :cljs (str name ".cljs"))))

(defn load-src
  ([ns]
   (load-src ns :clj))
  ([ns fmt]
   (try
     (->
      (ns->filename ns fmt)
      (io/resource)
      slurp)
     (catch Exception _
       (str "(ns " ns ")
            Namespace not found on classpath
                 ")))))

(defn src->src-list
  ([src]
   (src->src-list src :clj))
  ([src fmt]
   (->>
    (text->notebook fmt src)
    :segments
    (filter #(= (:type %) :code))
    (map #(get-in % [:data :code])))))

(defn src-list->notebook [src-list]
  {:content (->> (map (fn [src]
                        {:code src}) src-list)
                 (into []))})

(defn create-notebook
  ([ns]
   (create-notebook ns :clj))
  ([ns fmt]
   (rdm/delete-directory-ns ns)
   (-> ns
       (load-src fmt)
       (src->src-list fmt)
       src-list->notebook
       (assoc :meta {:id (guuid)
                     :eval-time "not evaluated"
                     :ns ns}))))

;; persistence

(defn load-notebook
  ([ns]
   (load-notebook ns :clj))
  ([ns fmt]
   (let [nb (rdm/loadr ns "notebook" :edn)]
     (-> (if nb
           nb
           (create-notebook ns fmt))
         (with-meta {:render-as :p/notebook})))))

(defn save-notebook [ns nb]
  (info "saving notebook: " ns)
  (rdm/save nb ns "notebook" :edn))

; eval

(defn eval-result->hiccup [{:keys [value] :as eval-result}]
  (when-let [hiccup (value->hiccup value)]
    (-> eval-result
        (assoc :hiccup hiccup)
        (dissoc :value))))

(defn eval-ns-raw
  "evaluates a clj namespace.
     returns seq of eval-result"
  [ns]
  (let [src (load-src ns)
        src-list (src->src-list src)]
    (map #(clj-eval {:ns ns
                     :code %})
         src-list)))

(defn eval-nb-segments [nb ns]
  (let [nsa (atom ns)
        segments (:content nb)
        nb-eval-segment (fn [seg]
                          (let [er (clj-eval (assoc seg :ns @nsa))]
                            (reset! nsa (:ns er))
                            er))]
    (map nb-eval-segment segments)))

(defn eval-notebook
  ([ns]
   (eval-notebook ns eval-result->hiccup)) ; default converter
  ([ns eval-result-view-fn]
   (let [nb (create-notebook ns)
         eval-results (eval-nb-segments nb ns)
         content (if eval-result-view-fn
                   (map eval-result-view-fn eval-results) ; use here a better thing.
                   eval-results)
         nb (-> nb
                (assoc :content (into [] content))
                (assoc-in [:meta :eval-time] (now-str))
                (assoc-in [:meta :java] (-> (System/getProperties) (get "java.version")))
                (assoc-in [:meta :clojure] (clojure-version)))]
     (save-notebook ns nb)
     (with-meta nb {:render-as :p/notebook}))))

(comment

  (ns->filename "demo.notebook-test.banana" :clj)

  (load-src "demo.notebook.image")
  (load-src "demo.notebook-test.banana")

  (-> (load-src "demo.notebook-test.banana")
      type)

  ;;  "(+ 1 1)
  ;; (println 123)
  ;; [1 2 3]
  ;; {:a 3}"

  (-> "demo.notebook-test.banana"
      load-src
      src->src-list)

  ;; ["(+ 1 1)"
  ;;  "(println 123)"
  ;;  "[1 2 3]"
  ;;  "{:a 3}"]

  (clj-eval {:code "(+ 4 4)"})
  (clj-eval {:code "(+ 4 4)" :ns "bongo"})

  ;; {:src "(+ 4 4)", 
  ;;  :result 8       note: result has not been processed in any way. result is a single eval result. 
  ;;  :out "", 
  ;;  :id :87929fe8-4003-4c25-9df4-512391857d07 }

  (create-notebook "demo.notebook-test.apple")
  (eval-notebook "demo.notebook-test.apple")

  (eval-notebook "demo.notebook.image")

  (load-notebook "user.notebook.banana")

;
  )
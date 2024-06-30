(ns reval.document.notebook
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre :refer [debug info warnf]]
   [modular.helper.id :refer [guuid]]
   [modular.helper.date :refer [now-str]]
   [reval.viz.data :refer [value->data]]
   [reval.document.manager :as rdm]
   [reval.document.path :refer [ns->dir ns->filename]]
   [reval.document.src-parser :refer [text->notebook]]
   [reval.kernel.clj-eval :refer [clj-eval]]))

;; create

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
       (str "(ns " ns ")\n ; This namespace does not exist as a local file!\n")))))

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
  ([this ns]
   (create-notebook this ns :clj))
  ([this ns fmt]
   (when ns
     (rdm/delete-directory-ns this ns))
   (let [src (if ns
               (load-src ns fmt)
               "")]
     (-> src
         (src->src-list fmt)
         src-list->notebook
         (assoc :meta {:id (guuid)
                       :eval-time "not evaluated"
                       :ns ns})))))

;; persistence

(defn load-notebook
  ([this ns]
   (load-notebook this ns :clj))
  ([this ns fmt]
   (let [nb (if ns
              (rdm/loadr this ns "notebook" :edn)
              nil)]
     (-> (if nb
           nb
           (create-notebook this ns fmt))
         (with-meta {:render-as :p/notebook})))))

(defn save-notebook [this ns nb]
  (info "saving notebook: " ns)
  (rdm/save this nb ns "notebook" :edn))

; eval

(defn eval-result->hiccup [{:keys [value] :as eval-result}]
  (when-let [data (value->data value)]
    (-> eval-result
        (dissoc :value)
        (merge  data))))

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
  ([this ns]
   (eval-notebook this ns eval-result->hiccup)) ; default converter
  ([this ns eval-result-view-fn]
   (let [nb (create-notebook this ns)
         eval-results (eval-nb-segments nb ns)
         content (if eval-result-view-fn
                   (map eval-result-view-fn eval-results) ; use here a better thing.
                   eval-results)
         nb (-> nb
                (assoc :content (into [] content))
                (assoc-in [:meta :eval-time] (now-str))
                (assoc-in [:meta :java] (-> (System/getProperties) (get "java.version")))
                (assoc-in [:meta :clojure] (clojure-version)))]
     (save-notebook this ns nb)
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

  (def this {:config {}})

  (create-notebook this "demo.notebook-test.apple")
  (eval-notebook this "demo.notebook-test.apple")

  (eval-notebook this "demo.notebook.image")

  (load-notebook this "user.notebook.banana")

;
  )

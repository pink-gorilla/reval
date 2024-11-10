(ns reval.document.notebook
  (:require
   [clojure.java.io :as io]
   [tick.core :as t]
   [taoensso.timbre :refer [debug info warnf]]
   [modular.helper.id :refer [guuid]]
   [dali.spec :refer [create-dali-spec]]
   [reval.dali.eval :refer [dalify]]
   [reval.document.manager :as rdm]
   [reval.document.path :refer [ns->dir ns->filename]]
   [reval.document.src-parser :refer [text->notebook]]
   [reval.kernel.clj-eval :refer [clj-eval]]))

;; create

(defn load-src
  ([nbns]
   (load-src nbns :clj))
  ([nbns fmt]
   (info "load-src nbns: " nbns " fmt: " fmt)
   (try
     (->
      (ns->filename nbns fmt)
      (io/resource)
      slurp)
     (catch Exception _
       (str "(ns " nbns ")\n ; This namespace does not exist as a local file!\n")))))

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
  ([this nbns]
   (create-notebook this nbns :clj))
  ([this nbns fmt]
   (when nbns
     (rdm/delete-directory-ns this nbns))
   (let [src (if nbns
               (load-src nbns fmt)
               "")]
     (-> src
         (src->src-list fmt)
         src-list->notebook
         (assoc :meta {:id (guuid)
                       :eval-time "not evaluated"
                       :ns nbns})))))

;; persistence

(defn plot-notebook [nb]
  (create-dali-spec
   {:viewer-fn 'reval.dali.viewer.notebook/notebook
    :data nb}))

(defn load-notebook
  ([this nbns]
   (load-notebook this nbns :clj))
  ([this nbns fmt]
   (let [nb (if nbns
              (rdm/loadr this nbns "notebook" :edn)
              nil)]
     (-> (if nb
           nb
           (create-notebook this nbns fmt))
         (plot-notebook)))))

(defn save-notebook [this nbns nb]
  (info "saving notebook: " nbns)
  (rdm/save this nb nbns "notebook" :edn))

; eval

(defn eval-ns-raw
  "evaluates a clj namespace.
     returns seq of eval-result"
  [nbns]
  (let [src (load-src nbns)
        src-list (src->src-list src)]
    (map #(clj-eval {:ns nbns
                     :code %})
         src-list)))

(defn eval-nb-segments [nb nbns]
  (let [nsa (atom nbns)
        segments (:content nb)
        nb-eval-segment (fn [seg]
                          (let [er (clj-eval (assoc seg :ns @nsa))]
                            (reset! nsa (:ns er))
                            er))]
    (map nb-eval-segment segments)))

(defn eval-notebook
  ([this nbns]
   (eval-notebook this nbns #(dalify this %))) ; default converter
  ([this nbns eval-result-view-fn]
   (let [nb (create-notebook this nbns)
         eval-results (eval-nb-segments nb nbns)
         content (if eval-result-view-fn
                   (map eval-result-view-fn eval-results) ; use here a better thing.
                   eval-results)
         nb (-> nb
                (assoc :content (into [] content))
                (assoc-in [:meta :eval-time] (-> (t/instant) str))
                (assoc-in [:meta :java] (-> (System/getProperties) (get "java.version")))
                (assoc-in [:meta :clojure] (clojure-version)))]
     (save-notebook this nbns nb)
     (plot-notebook nb))))

(comment

  (ns->filename "demo.notebook-test.banana" :clj)

  (load-src "notebook.study.image")
  (load-src "notebook.study.image" :clj)
  (load-src "notebook.study.image" :cljs)

  (-> (load-src "demo.notebook-test.banana")
      type)

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

  (def this {})

  (create-notebook this "demo.notebook-test.apple")
  (eval-notebook this "demo.notebook-test.apple")

  (eval-notebook this "demo.notebook.image")

  (load-notebook this "user.notebook.banana")

;
  )

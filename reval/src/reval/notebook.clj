(ns reval.notebook
  (:require
   [clojure.java.io :as io]
   [tick.core :as t]
   [taoensso.timbre :refer [debug info warnf]]
   [modular.helper.id :refer [guuid]]
   ; dali
   [dali.spec :refer [create-dali-spec]]
   [reval.dali.eval :refer [dalify]]
   ; reval
   [reval.namespace.path :refer [ns->dir ns->filename]]
   [reval.namespace.store :as namespace-store]
   [reval.notebook.src-parser :refer [text->notebook]]
   [reval.notebook.store :as store]
   [reval.kernel.clj-eval :refer [clj-eval]]
   [reval.config :refer [reval]]
   ))

;; create

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
  ([nbns]
   (create-notebook nbns :clj))
  ([nbns fmt]
   (when nbns
     (store/delete-directory-ns reval nbns))
   (let [src (if nbns
               (namespace-store/load-src nbns fmt)
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
  ([nbns]
   (load-notebook nbns :clj))
  ([nbns fmt]
   (let [nb (if nbns
              (store/loadr reval nbns "notebook" :edn)
              nil)]
     (-> (if nb
           nb
           (create-notebook nbns fmt))
         (plot-notebook)))))

(defn save-notebook [nbns nb]
  (info "saving notebook: " nbns)
  (store/save reval nb nbns "notebook" :edn))

; eval

(defn eval-ns-raw
  "evaluates a clj namespace.
     returns seq of eval-result"
  [nbns]
  (let [src (namespace-store/load-src nbns)
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
  ([nbns]
   (eval-notebook nbns #(dalify reval %))) ; default converter
  ([nbns eval-result-view-fn]
   (let [nb (create-notebook nbns)
         eval-results (eval-nb-segments nb nbns)
         content (if eval-result-view-fn
                   (map eval-result-view-fn eval-results) ; use here a better thing.
                   eval-results)
         nb (-> nb
                (assoc :content (into [] content))
                (assoc-in [:meta :eval-time] (-> (t/instant) str))
                (assoc-in [:meta :java] (-> (System/getProperties) (get "java.version")))
                (assoc-in [:meta :clojure] (clojure-version)))]
     (save-notebook nbns nb)
     (plot-notebook nb))))

(comment

 

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

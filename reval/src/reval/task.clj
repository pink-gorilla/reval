(ns reval.task
  (:require
   [clojure.java.io :as io]
   [modular.system]
   [reval.document.collection  :refer [nb-collections eval-collections]]
   [reval.default] ; side-effects
   [clojure.pprint :refer [print-table]]))

(def this (modular.system/system :reval))

(def nb-welcome
  {:meta {:ns "welcome"}
   :content
   [{:code "(println \"Welcome to Notebook Viewer \")"
     :hiccup [:h1.text-blue-800 "Welcome to Notebook Viewer!"]
     :out "Welcome to Notebook Viewer"}]})

(defn save-welcome []
  (spit "target/webly/public/rdocument/welcome.edn" nb-welcome))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn eval-all-collections [m]
  (println "evaluating nb collections .. m: " (keys m))
  (let [cols (nb-collections this)]
    (ensure-directory "target")
    (ensure-directory "target/webly")
    (ensure-directory "target/webly/public")
    (ensure-directory "target/webly/public/rdocument")
    (spit "target/webly/public/rdocument/notebooks.edn" cols)
    (save-welcome)
    (println "nb-cols: " cols)
    (eval-collections this cols)))

(defn- inline-coll [[cname coll]]
  (let [x (partition 2 coll)
        y (map (fn [[kernel nbs]]
                 (map #(assoc % :kernel kernel :coll cname) nbs)) x)]
    (apply concat y)))

(defn- inline-collections [cols]
  (reduce concat [] (map inline-coll cols)))

(defn print-all-collections [m]
  (println "nb collections .. m: " (keys m))
  (let [cols (nb-collections this)
        cols-f (inline-collections cols)]
    (print-table [:coll :nbns :ext :kernel :path] cols-f)))

(comment
  (inline-collections (nb-collections this))
  (print-all-collections {})
  (eval-all-collections {})

; 
  )





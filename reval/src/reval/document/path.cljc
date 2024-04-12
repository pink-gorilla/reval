(ns reval.document.path
  (:require
   [clojure.string :as str]))

; discover clj/cljs files in resources (can be jar or file)

(defn ns->dir [ns]
  (-> ns
      (str/replace #"\." "/")
      (str/replace #"\-" "_")))

(defn split-ext [filename]
  (let [m (re-matches #"(.*)\.(clj[sc]*)" filename)
        [_ name ext] m]
    [name ext]))

(defn is-format? [fmt [_ ext]] ; name
  (case ext
    "cljs" (= fmt :cljs)
    "clj" (= fmt :clj)
    "cljc" true))

(defn ext-is-format? [fmt ext]
  (when ext
    (case ext
      "cljs" (= fmt :cljs)
      "clj" (= fmt :clj)
      "cljc" true)))

(defn ns->filename [ns fmt]
  (let [name (ns->dir ns)]
    (case fmt
      :clj (str name ".clj")
      :cljs (str name ".cljs")
      :cljc (str name ".cljc"))))

(defn filename->ns [dir name]
  (str
   (str/replace dir #"/" ".")
   (str/replace name #"_" "-")))

(comment

  (split-ext "demo/notebook.clj")
  (split-ext "demo/notebook.clj#")

  (ns->dir "demo.notebook-test.apple")

;  
  )

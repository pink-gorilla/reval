(ns reval.save
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]))

(def ^:dynamic *clones-root*
  "Directory where classpath-only resources are written, mirroring resource paths."
  ".reval/clones")

(defn clone-file-path
  "Filesystem path under *clones-root* for a resource path (e.g. demo/notebook/foo.clj)."
  [res-path]
  (when-not (str/blank? (str res-path))
    (str *clones-root* "/"
         (-> res-path str str/trim (str/replace #"^/+|/+$" "")))))

(defn slurp-clone-if-present
  "If a clone file exists for this resource path, return its contents; otherwise nil."
  [res-path]
  (when-let [p (clone-file-path res-path)]
    (let [f (io/file p)]
      (when (.exists f) (slurp f)))))

(defn resolve-save-target
  "Prefer a concrete local :path; otherwise write under *clones-root* using :res-path."
  [{:keys [path res-path]}]
  (let [path-s (when path (str/trim (str path)))
        res-s (when res-path (str/trim (str res-path)))]
    (cond
      (not (str/blank? path-s))
      {:target path-s :clone? false}

      (not (str/blank? res-s))
      (when-let [t (clone-file-path res-s)]
        {:target t :clone? true})

      :else nil)))

(defn save-code
  "Save source. Uses :path when present (local file); otherwise :res-path under *clones-root*."
  [{:keys [code path res-path] :as opts}]
  (if-let [{:keys [target clone?]} (resolve-save-target opts)]
    (do
      (when-let [dir (.getParentFile (io/file target))]
        (fs/create-dirs (.getPath dir)))
      (println "saving code to:" target (if clone? "[clone]" ""))
      (spit target code)
      {:path target :clone? clone?})
    (throw (ex-info "save-code needs non-blank :path or :res-path"
                    {:path path :res-path res-path}))))

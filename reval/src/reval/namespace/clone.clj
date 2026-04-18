(ns reval.namespace.clone
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [reval.namespace.explore :refer [repl-tree]]
   [reval.namespace.store :refer [clone-file-path save-code]]
   [reval.namespace.path :refer [ns->filename]]))

(defn clone-present?
  "If a clone file exists for this resource path, return its contents; otherwise nil."
  [res-path]
  (when-let [p (clone-file-path res-path)]
    (let [f (io/file p)]
      (.exists f))))

(defn save-code-to-clone [res-path]
  (when-not (clone-present? res-path)
    (let [code (-> res-path io/resource slurp)]
      (save-code {:res-path res-path :code code}))))

(defn- classpath-notebook-nodes
  "Leaf notebook nodes loaded from the classpath (no local :path), not from disk."
  [repl-tree-data]
  (for [root (:roots repl-tree-data)
        node (tree-seq :dir? :children (:tree root))
        :when (and (not (:dir? node))
                   (str/blank? (str (:path node))))]
    node))

(defn save-resource-notebooks-to-clone!
  "Clone every classpath-sourced notebook from (repl-tree) that does not yet have a clone."
  []
  (doseq [{:keys [name-full]} (classpath-notebook-nodes (repl-tree))]
    (save-code-to-clone name-full)))

(comment
  (save-resource-notebooks-to-clone!)
  (clone-file-path "quanta.notebook.asset-db.eodhd-list-db")
  (clone-present? "quanta/notebook/asset_db/eodhd_list_db.clj")
  (-> "quanta.notebook.asset-db.eodhd-list-db"
      (ns->filename :clj)
      (save-code-to-clone)))

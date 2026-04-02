(ns reval.document.explore
  "Build a directory tree of notebook sources under classpath resource roots."
  (:require
   [clojure.string :as str]
   [modular.resource.explore :as rs]
   [reval.document.path :as p]))

(defn notebook-file? [^String name]
  (boolean (re-find #"\.cljc?$" name)))

(defn- normalize-root [root]
  (-> root str (str/replace #"/+$" "")))

(defn name-full->nbns [name-full]
  (when name-full
    (let [idx (.lastIndexOf name-full "/")
          fname (if (neg? idx) name-full (subs name-full (inc idx)))
          parent (if (neg? idx) "" (subs name-full 0 (inc idx)))
          [stem _ext] (p/split-ext fname)]
      (when stem
        (p/filename->ns parent stem)))))

(defn- file-node [{:keys [name name-full path]}]
  (let [[_ ext] (p/split-ext name)]
    {:name name
     :name-full name-full
     :dir? false
     :path path
     :ext ext
     :nbns (name-full->nbns name-full)}))

(defn- distinct-entries [entries]
  (->> entries
       (group-by :name-full)
       vals
       (map first)))

(defn- sorted-dir-entries [entries]
  (->> entries
       (filter rs/dir?)
       distinct-entries
       (sort-by :name)))

(defn- sorted-notebook-files [entries]
  (->> entries
       (remove rs/dir?)
       (filter (comp notebook-file? :name))
       distinct-entries
       (sort-by :name)
       (map file-node)))

(defn build-tree
  "Recursive tree for one resource directory (no trailing slash required)."
  [res-dir]
  (let [dir (normalize-root res-dir)
        entries (try (vec (rs/describe dir))
                   (catch Exception _ []))
        label (if (str/includes? dir "/")
                (subs dir (inc (.lastIndexOf dir "/")))
                dir)
        dir-children (mapv (fn [e]
                             (-> (build-tree (:name-full e))
                                 (assoc :path (:path e))))
                           (sorted-dir-entries entries))]
    {:name label
     :name-full dir
     :dir? true
     :path nil
     :children (vec (concat dir-children
                            (sorted-notebook-files entries)))}))

(defn root-forest
  "Vector of root maps {:id :res-path :tree} for each configured namespace root."
  [namespace-roots]
  (vec
   (for [root (map normalize-root namespace-roots)]
     {:id root
      :res-path root
      :tree (build-tree root)})))

(defn namespace-explorer-edn
  "Pure data for the directory explorer UI and for writing `namespace-explorer.edn`."
  [namespace-roots]
  {:namespace-roots (vec (map normalize-root namespace-roots))
   :roots (root-forest namespace-roots)})

(defn nb-collections
  "Notebook directory tree for clients (replaces flat namespace list when using explorer UI)."
  [env]
  (namespace-explorer-edn (or (:namespace-root env) ["user" "demo"])))

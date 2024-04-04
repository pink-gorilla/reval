(ns reval.document.collection
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [modular.resource.explore :refer [describe-files]]
   [reval.document.classpath :refer [split-ext is-format? ext-is-format? filename->ns]]
   [reval.document.notebook :refer [eval-notebook]]
   [reval.config :refer [get-in-config]]))

; this does not work. meta-data cannot be assoced to a string
#_(defn name-with-meta [{:keys [name path] :as entry}]
    (if path
      (with-meta name {:save-path path})
      name))

(defn convert-ns [res-path {:keys [name path] :as entry}]
  (let [[name-only ext] (split-ext name)
        nbns (if (and name-only ext)
               (filename->ns res-path name-only)
               :not-code)]
    (if path
      {:nbns nbns
       :ext ext
       :path path}
      {:nbns nbns
       :ext ext})))

#_(defn get-ns-list [fmt res-path]
    (->> (describe-files res-path)
         (map :name)
       ;(map name-with-meta)
         (map split-ext)
         (filter (partial is-format? fmt))
         (map first)
         (map #(filename->ns res-path %))))

(defn get-ns-list [fmt res-path]
  (->> (describe-files res-path)
       (map #(convert-ns res-path %))
       (filter #(ext-is-format? fmt (:ext %)))))

(defn get-nss-list [fmt res-paths]
  (->
   (->> (map #(get-ns-list fmt %) res-paths)
        (apply concat [])
        (sort-by :nbns)
        vec)
   (with-meta {:fmt fmt})))

(defn get-collections [spec]
  (->> (map (fn [[k v]]
              [k [(first v) (get-nss-list (first v) (rest v))]]) spec)
       (into {})))

(defn eval-collection [[name [t ns-list]]]
  (doall
   (map #(eval-notebook (:nbns %)) ns-list)))

(defn eval-collections [colls]
 ; {:demo [:clj []]
 ;  :user [:clj ["test.notebook.apple"]]}
  (doall
   (map eval-collection colls)))

(defn nb-collections []
  (get-collections (get-in-config [:reval :collections])))

(comment

  (describe-files "demo/notebook")
  (describe-files "notebook/study") ; file (has path for saving)
  (describe-files "notebook/test")

  (split-ext "apple_blue.cljs")
  (split-ext "apple_blue.clj")
  (split-ext "apple_blue.cljc")

  (->> (split-ext "apple_blue.cljs")
       (is-format? :clj))
  (->> (split-ext "apple_blue.cljs")
       (is-format? :cljs))
  (->> (split-ext "apple_blue.cljc")
       (is-format? :cljs))
  (->> (split-ext "apple_blue.cljc")
       (is-format? :clj))

  (filename->ns "demo/notebook/" "apple_blue.clj")

  (get-ns-list :clj "demo/notebook/")
  (get-ns-list :cljs "demo/notebook/")

  (get-ns-list :clj "notebook/study/")
  (get-ns-list :clj "notebook/test/")

  (-> (get-nss-list :clj ["demo/notebook/"])
      meta)

  (-> (get-nss-list :cljs ["demo/notebook/"])
      meta)

  (get-nss-list :clj ["notebook/test27/"])

  (get-collections
   {:demo [:clj "demo/notebook/"]
    :user [:clj "notebook/"]})

  (-> (get-collections
       {:user [:clj "user/notebook/"]
        :demo [:clj "demo/notebook/"]})
      pr-str)

  (get-collections
   {:test5 [:clj "notebook/test27/"]
    :study [:clj "notebook/study/"]})

  (nb-collections)

  (-> (nb-collections)
      eval-collections)

;
  )


















(ns reval.document.collection
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [modular.config :refer [get-in-config]]
   [modular.resource.explore :refer [describe-files]]
   [reval.document.classpath :refer [split-ext is-format? filename->ns]]
   [reval.document.notebook :refer [eval-notebook]]))

(defn get-ns-list [fmt res-path]
  (->> (describe-files res-path)
       (map :name)
       (map split-ext)
       (filter (partial is-format? fmt))
       (map first)
       (map #(filename->ns res-path %))))

(defn get-nss-list [fmt res-paths]
  (->
   (->> (map #(get-ns-list fmt %) res-paths)
        (apply concat [])
        sort
        vec)
   (with-meta {:fmt fmt})))

(defn get-collections [spec]
  (->> (map (fn [[k v]]
              [k [(first v) (get-nss-list (first v) (rest v))]]) spec)
       (into {})))

(defn eval-collection [[name [t ns-list]]]
  (doall
   (map eval-notebook ns-list)))

(defn eval-collections [colls]
 ; {:demo [:clj []]
 ;  :user [:clj ["test.notebook.apple"]]}
  (doall
   (map eval-collection colls)))

(defn nb-collections []
  (get-collections (get-in-config [:reval :collections])))

(comment

  (get-ns-files "demo/notebook")
  (get-ns-files "demo/notebook/")

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

  (-> (get-nss-list :clj ["demo/notebook/"])
      meta)

  (-> (get-nss-list :cljs ["demo/notebook/"])
      meta)

  (get-collections
   {:demo [:clj "demo/notebook/"]
    :user [:clj "notebook/"]})

  (-> (get-collections
       {:user [:clj "user/notebook/"]
        :demo [:clj "demo/notebook/"]})
      pr-str)

;
  )


















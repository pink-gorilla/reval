(ns reval.document.collection
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [modular.resource.explore :refer [describe-files]]
   [reval.document.path :refer [split-ext is-format? ext-is-format? filename->ns]]
   [reval.document.notebook :refer [eval-notebook create-notebook save-notebook]]))

(defn- convert-ns [res-path {:keys [name path] :as entry}]
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

(defn- get-ns-list [fmt res-path]
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

(defn build-collection [col-spec]
  (->> col-spec
       (map (fn [[fmt res-paths]]
              (let [res-paths (if (string? res-paths)
                                [res-paths]
                                res-paths)]
                [fmt (get-nss-list fmt res-paths)])))
       (into {})))

(defn collection-ns [{:keys [clj cljs] :as col-info}]
  (let [nb-list (fn [info-seq]
                  (->> info-seq
                       (sort-by :nbns)
                       ;(map :nbns info-seq)
                       ))]
    (concat
     (nb-list clj)
     (nb-list cljs))))

(defn collections-ns-summary [m]
  (let [one (fn [[name i]]
              [name
               (-> i
                   (build-collection)
                   (collection-ns))])]
    (->> m
         (map one)
         (into {}))))

;; EVAL

(defn create-empty-notebook [this nbns fmt]
  (->> (create-notebook this nbns fmt)
       (save-notebook this nbns)))

(defn eval-collection [this col-spec]
  (let [coll-nb (build-collection col-spec)
        #_{:clj
           [{:nbns "notebook.study.exception",
             :ext "clj",
             :path "/home/florian/repo/pink-gorilla/reval/demo/src/notebook/study/exception.clj"}
            {:nbns "notebook.study.fira-code",
             :ext "clj",
             :path "/home/florian/repo/pink-gorilla/reval/demo/src/notebook/study/fira_code.clj"}]
           :cljs []}
        clj-nbns-seq (map :nbns (:clj coll-nb))
        cljs-nbns-seq (map :nbns (:cljs coll-nb))]
    (doall
     (map #(eval-notebook this %) clj-nbns-seq))
    (doall
     (map #(create-empty-notebook this % :cljs) cljs-nbns-seq))
    nil))

(defn eval-collections [this colls]
 ; {:demo [:clj []]
 ;  :user [:clj ["test.notebook.apple"]]}
  (doall
   (map (partial eval-collection this) (vals colls))))

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

  (build-collection {:clj "notebook/study/"
                     :cljs "demo/notebook/"})

  (build-collection {:clj ["notebook/study/"
                           "notebook/big_list/"]
                     :cljs "demo/notebook/"})
  ;; => {:clj
  ;;     [{:nbns "notebook.big_list.a",
  ;;       :ext "clj",
  ;;       :path "/home/florian/repo/pink-gorilla/reval/demo/src/notebook/big_list/a.clj"}
  ;;      {:nbns "notebook.study.text",
  ;;       :ext "clj",
  ;;       :path "/home/florian/repo/pink-gorilla/reval/demo/src/notebook/study/text.clj"}],
  ;;     :cljs
  ;;     [{:nbns "demo.notebook.ajax", :ext "cljs"}
  ;;      {:nbns "demo.notebook.clojure-edn", :ext "cljs"}
  ;;      {:nbns "demo.notebook.clojure-string", :ext "cljs"}
  ;;      {:nbns "demo.notebook.dialog", :ext "cljs"}
  ;;      {:nbns "demo.notebook.frisk", :ext "cljs"}
  ;;      {:nbns "demo.notebook.goog-string", :ext "cljs"}
  ;;      {:nbns "demo.notebook.javelin", :ext "cljs"}
  ;;      {:nbns "demo.notebook.jsinterop", :ext "cljs"}
  ;;      {:nbns "demo.notebook.notify", :ext "cljs"}
  ;;      {:nbns "demo.notebook.page-nav", :ext "cljs"}
  ;;      {:nbns "demo.notebook.pprint", :ext "cljs"}
  ;;      {:nbns "demo.notebook.promesa", :ext "cljs"}
  ;;      {:nbns "demo.notebook.tailwind", :ext "cljs"}]}

  (->  (build-collection {:clj ["notebook/study/"
                                "notebook/big_list/"]
                          :cljs "demo/notebook/"})
       (collection-ns))

  (collections-ns-summary
   {:study {:clj "notebook/study/"}
    :big-list {:clj "notebook/big_list/"}
    :cljs {:cljs "notebook/cljs/"}
    :demo {:clj "demo/notebook/" ; embedded notebooks in jars.
           :cljs "demo/notebook/"}})

;
  )


















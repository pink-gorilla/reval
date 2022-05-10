(ns reval.editor
  (:require [clojure.string :as str]
            ; [cljs.reader :as edn]
            [clojure.tools.reader :as reader]
            [rewrite-clj.zip.move :as move]
            [rewrite-clj.zip :as zip]
            [rewrite-clj.zip.base :as zip-base]
            [rewrite-clj.node :as node]
            [clojure.tools.reader.reader-types :as r]
            [rewrite-clj.parser :as parser]

            #?(:cljs [rewrite-clj.node.uneval :refer [UnevalNode]])
            #?(:cljs [rewrite-clj.node.reader-macro :refer [ReaderMacroNode DerefNode]])
            #?(:cljs [rewrite-clj.node.fn :refer [FnNode]])
            #?(:cljs [rewrite-clj.node.quote :refer [QuoteNode]])
            #?(:cljs ["fs" :refer [readFileSync]]))
  #?(:clj (:import [rewrite_clj.node.uneval UnevalNode]
                   [rewrite_clj.node.reader_macro ReaderMacroNode DerefNode]
                   [rewrite_clj.node.fn FnNode]
                   [rewrite_clj.node.quote QuoteNode])))

(defn- reader-tag? [node]
  (when node
    (or (instance? ReaderMacroNode node)
        (instance? FnNode node)
        (instance? QuoteNode node)
        (instance? DerefNode node))))

(defn in-range? [{:keys [row col end-row end-col]} {r :row c :col}]
  (and (>= r row)
       (<= r end-row)
       (if (= r row) (>= c col) true)
       (if (= r end-row) (<= c end-col) true)))

(defn- find-inners-by-pos
  "Find last node (if more than one node) that is in range of pos and
  satisfying the given predicate depth first from initial zipper
  location."
  [zloc pos]
  (->> zloc
       (iterate zip/next)
       (take-while identity)
       (take-while (complement move/end?))
       (filter #(in-range? (-> % zip/node meta) pos))))


(defn- filter-forms [nodes]
  (when nodes
    (let [valid-tag? (comp #{:vector :list :map :set :quote} :tag)]
      (->> nodes
           (map zip/node)
           (partition-all 2 1)
           (map (fn [[fst snd]]
                  (cond
                    (reader-tag? fst) fst
                    (-> fst :tag (= :list) (and snd (reader-tag? snd))) snd
                    (valid-tag? fst) fst)))
           (filter identity)
           first))))

(defn- zip-from-code [code]
  (let [reader (r/indexing-push-back-reader code)
        nodes (->> (repeatedly #(try
                                  (parser/parse reader)
                                  (catch #?(:clj Throwable :cljs :default) _
                                    (r/read-char reader)
                                    (node/whitespace-node " "))))
                   (take-while identity)
                   (doall))
        all-nodes (with-meta
                    (node/forms-node nodes)
                    (meta (first nodes)))]
    (-> all-nodes (zip-base/edn {:track-position? true}))))


(defn block-for
  "Gets the current block from the code (a string) to the current row and col (0-based)"
  [code [row col]]
  (let [node-block (-> code
                       zip-from-code
                       (find-inners-by-pos {:row (inc row) :col (inc col)})
                       reverse
                       filter-forms)
        {:keys [row col end-row end-col]} (some-> node-block meta)]
    (when node-block
      [[[(dec row) (dec col)] [(dec end-row) (- end-col 2)]]
       (node/string node-block)])))


(comment
 (block-for "(+ 3 1)\n(* 3 4 5 \n   6 7)\n(println 55)" [1 5])  
;
  )

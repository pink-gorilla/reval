(ns reval.type.util
  (:require
   [dali.spec :refer [create-dali-spec]]
   [reval.type.converter :refer [type->dali]]))

(def styles
  {"clj-raw"    {:color "red"}
   "clj-nil"    {:color "grey"}
   "clj-symbol" {:color  "steelblue"}
   "clj-namespace" {:color "steelblue"}
   "clj-keyword" {:color "rgb(30, 30, 82)"}
   "clj-var" {:color "deeppink"}
   "clj-atom" {:color "darkorange"}
   "clj-agent" {:color "darkorange"}
   "clj-ref" {:color "darkorange"}

   "clj-char" {:color "dimgrey"}
   "clj-string" {:color "grey"}

   "clj-int" {:color "blue"}
   "clj-long" {:color "blue"}
   "clj-bigint" {:color "blue"}

   "clj-float" {:color "darkgreen"}
   "clj-double" {:color "darkgreen"}
   "clj-bigdecimal" {:color "darkgreen"}
   "clj-ratio" {:color "darkgreen"}

   "clj-localdate" {:color "green"}})

(defn class->style [c]
  (if-let [s (get styles c)]
    {:style s}
    {}))

(defn simplevalue->dali
  [v class]
  (create-dali-spec
   {:viewer-fn 'dali.viewer.hiccup/hiccup
    :data [:span (class->style class)
           (pr-str v)]}))

;; list

(defn- box [{:keys [class open close children] :as data}]
  (create-dali-spec
   {:viewer-fn 'reval.dali.viewer.list/list-view
    :data data}))

(defn list->dali
  [{:keys [class open close separator] :as opts} list]
  (box (assoc opts
              :children (map type->dali list))))

(defn map->dali [opts m]
  (box (assoc opts
              :children (interleave
                         (map type->dali (keys m))
                         (map type->dali (vals m))))))

(comment
  (list->dali
   {:class "clj-lazy-seq"
    :open "("
    :close ")"
    :separator " "}
   [1 "test" 5.3 nil :super])

  (map->dali
   {:class "clj-map"
    :open "{"
    :close "}"
    :separator " "}
   {:a 1
    :b "test"
    :c 5.3
    :d nil
    :e :super})

;  
  )

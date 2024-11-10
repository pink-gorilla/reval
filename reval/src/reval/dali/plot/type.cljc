(ns reval.dali.plot.type
  (:require
   [dali.spec :refer [create-dali-spec]]
   [reval.type.protocol :refer [to-dali]]))

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
  [env {:keys [class open close separator] :as opts} list]
  (box (assoc opts
              :children (map #(to-dali % env) list))))

(defn map->dali [env opts m]
  (box (assoc opts
              :children (interleave
                         (map #(to-dali % env) (keys m))
                         (map #(to-dali % env) (vals m))))))

(comment
  (list->dali
   nil
   {:class "clj-lazy-seq"
    :open "("
    :close ")"
    :separator " "}
   [1 "test" 5.3 nil :super])

  (map->dali
   nil
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

;; UNKNOWN

(defn unknown-type [v]
  (let [type-as-str (-> v type str)]
    (create-dali-spec
     {:viewer-fn 'dali.viewer.hiccup/hiccup
      :data  [:div.border-solid.p-2.dali-unknown-type
              [:p.text-red-300 "unknown type: " type-as-str]
              [:span (pr-str v)]]})))

;; ERROR

(defn type-convert-err [v]
  (let [type-as-str (-> v type str)]
    (create-dali-spec
     {:viewer-fn 'dali.viewer.hiccup/hiccup
      :data  [:div.border-solid.p-2.dali-type-convert-err
              [:p.text-red-300 type-as-str]
              [:span (pr-str v)]]})))
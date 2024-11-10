(ns reval.dali.viewer.collection
  (:require
   [clojure.string :refer [split]]))

;; COLLECTION UI

(defn nb-item [open-link {:keys [nbns] :as nbinfo}]
  [:a
   [:p.w-full.truncate.bg-blue-200.hover:bg-blue-300.border.border-solid.border-blue-300.p-1.cursor-pointer
   ; trunctate does the text magic
   ; .overflow-x-hidden
   ;[:a ;.m-1
    {:class "text-blue-500"
     :on-click #(open-link nbinfo)}
    ;(-> (split nbns ".") last)
    nbns
    ;]
    ]])

(defn nb-list [link [coll-name coll-seq]]
  (into
   [:<>
    [:p.bg-red-300 coll-name]]
   (map #(nb-item link %) coll-seq)))

(defn notebook-collection [{:keys [link data]}]
  [:div.w-full.h-full.w-min-64.max-h-full.overflow-y-auto
   (into
    [:div.flex.flex-col.items-stretch.bg-gray-50.h-full.w-full.max-h-full.overflow-y-auto]
    (map #(nb-list link %) data))])

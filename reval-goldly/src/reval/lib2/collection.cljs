;; COLLECTION UI

(def show-collection-debug-ui false)

(defn nb-item [open-link fmt ns]
  [:p.w-full.truncate ; .overflow-x-hidden
   [link-dispatch [:bidi/goto open-link :query-params {:ns ns :fmt (name fmt)}]
    (-> (string/split ns ".") last)
   ; ns
    ]])

(defn nb-list [open-link [name [fmt list]]]
  (into
   [:div.w-full
    [:p.bg-red-300 name]
    (when show-collection-debug-ui
      [:p (meta list) (pr-str list)])]
   (map #(nb-item open-link fmt %) list)))

(defn notebook-collection [open-link d]
  [:div.w-full.h-full.w-min-64
   (into
    [:div.flex.flex-col.items-stretch.bg-gray-50.h-full.w-full]
    (map #(nb-list open-link %) d))])
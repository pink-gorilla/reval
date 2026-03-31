(ns reval.dali.viewer.collection)

;; COLLECTION UI

(def ^:private collection-css
  ".reval-nb-item {
     width: 100%;
     overflow: hidden;
     text-overflow: ellipsis;
     white-space: nowrap;
     background: #bfdbfe;
     border: 1px solid #93c5fd;
     padding: 4px;
     cursor: pointer;
     color: #3b82f6;
     margin: 0;
   }
   .reval-nb-item:hover { background: #93c5fd; }
   .reval-nb-coll-name {
     background: #fca5a5;
     margin: 0;
     padding: 2px 4px;
   }")

(defn nb-item [open-link {:keys [nbns] :as nbinfo}]
  [:p.reval-nb-item
   {:on-click #(open-link nbinfo)}
   nbns])

(defn nb-list [link [coll-name coll-seq]]
  (into
   [:<>
    [:p.reval-nb-coll-name coll-name]]
   (map #(nb-item link %) coll-seq)))

(defn notebook-collection [{:keys [link data]}]
  [:<>
   [:style collection-css]
   [:div {:style {:width "100%"
                  :height "100%"
                  :min-width "16rem"
                  :max-height "100%"
                  :overflow-y "auto"}}
    (into
     [:div {:style {:display "flex"
                    :flex-direction "column"
                    :align-items "stretch"
                    :background "#f9fafb"
                    :height "100%"
                    :width "100%"
                    :max-height "100%"
                    :overflow-y "auto"}}]
     (map #(nb-list link %) data))]])

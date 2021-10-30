(defonce collection-state
  (r/atom {:evaluated ["demo.notebook.banana"]
           :cljs ["scratchpad"]
           :clj  ["scratchpad"]
           :snippets  ["scratchpad"]}))


(defn collection [notebook-ns-list on-select]
  [:table
   (for [n notebook-ns-list]
     [:tr
      [:a {:href (str "/viewer/notebook/" n)
           :on-click (fn [& args]
                       (when on-select 
                         (on-select n)))}
       [:td n]]])])

(defn collection-page [{:keys [route-params query-params handler] :as route}]
  ;(get-notebooks-once)
  [:div
   [:div.text-green-300 "notebooks..."]
   [:p "there are nr notebooks: " (count (get-in @collection-state [:evaluated]))]
   [:p.text-red-300 (pr-str @collection-state)]
   [collection (get-in @collection-state [:evaluated])]
   [:p "add code here..."]])

(add-page viewer-page :viewer)

;:resources [["1" :edn]]

(defn print-status [x]
  (println "status: " x))
(rf/dispatch [:ws/send [:ws/status []] print-status 5000])


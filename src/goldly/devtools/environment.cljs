





; ; (defmethod reagent-page :demo/main [& args]


#_(defn available-pages []
    (->> (methods reagent-page)
         keys
         (remove #(= :default %))
         (into [])))

(defn kw-item [t]
  [:p.m-1 (pr-str t)])

(defn keyword-list [name list]
  [:div.mt-10
   [:h2.text-2xl.text-blue-700.bg-blue-300 name]
   (into [:div.grid.grid-cols-4]
         (map kw-item (sort list)))])
 
;[:h2.text-2xl.text-blue-700.bg-blue-300 "pinkie renderer - lazy"]
;(into [:p] (map p (sort (lazy/available))))

;(run-a state [:extensions] :extension/summary)

;(run-a state [:services] :goldly/services)

(defn show-extensions [& d]
  [:div (pr-str d)])

(defn environment []
    [site/main-with-header
     [devtools-menu] 30
     [:div
        [keyword-list "hiccup-fh (functional hiccup list) " (pinkie/tags)]
        [keyword-list "pages" (page/available)]
        #_ [url-loader {:fmt :clj
                     :url :extension/summary}
           show-extensions ]
        [url-loader {:fmt :clj
                     :url :goldly/services}
           (partial keyword-list "services")]
      ]])
 

(defn environment-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
   [environment]])

(add-page environment-page :environment)

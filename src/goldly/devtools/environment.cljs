





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

(defn goldly-version [{:keys [version generated-at]}]
  [:div "goldly version: " version " " generated-at
   ;(pr-str v)
   ])

(defn ext [{:keys [name lazy]}]
  [:span.mr-2 name])

(defn extension-summary [exts]
  (into [:div
         [:h2.text-2xl.text-blue-700.bg-blue-300 "extensions"]
         ; (pr-str exts)
         ]
        (map ext exts)))

(defn ns-bindings-view [[sci cljs]] ; 
  [:p
   [:span.text-red-500 (pr-str sci)]
   [:span (pr-str cljs)]])

(defn sci-bindings [{:keys [data] :as sci-bindings}]
  (let [{:keys [namespaces bindings ns-bindings]} data]
    [:div
     [:h2.text-2xl.text-blue-700.bg-blue-300 "sci bindings"]
      ;(pr-str bindings)
     (into [:div.grid.grid-cols-2]
           (map ns-bindings-view bindings))]))

(defn extension-list [exts]
  [:div
   [:h2.text-2xl.text-blue-700.bg-blue-300 "extension details"]
   (into [:div.ml-5
          (pr-str exts)]
         []
        ;(map ext exts)
         )])
(defn environment []
  [site/main-with-header
   [devtools-menu] 30
   [:div
    [url-loader {:fmt :clj
                 :url :goldly/version}
     goldly-version]
    [url-loader {:fmt :clj
                 :url :goldly/extension-summary}
     extension-summary]

    [keyword-list "hiccup-fh (functional hiccup list) " (pinkie/tags)]
    [keyword-list "pages" (page/available)]

    [url-loader {:fmt :clj
                 :url :goldly/services}
     (partial keyword-list "services")]

    [url-loader {:fmt :clj
                 :url :goldly/sci-bindings}
     sci-bindings]

    [url-loader {:fmt :clj
                 :url :goldly/extension-list}
     extension-list]]])

(defn environment-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
   [environment]])

(add-page environment-page :environment)

;  sci-bindings
; :goldly/get-extension-info get-extension-info
; :goldly/get-extension-theme ext-theme





(defn page-item [i]
  [:span.m-1
   (str i)])

(defn page-list [p]
  (into
   [:div
    [:h1 "page list"]]
   (map page-item p)))

(defn page-list-page [{:keys [route-params query-params handler] :as route}]
  (let [p (page/available)]
  ;[:div.bg-green-300.w-screen.h-screen.overflow-scroll
    [site/main-with-header
     [devtools-menu] 30
     [page-list p]]))

(add-page page-list-page :pages)




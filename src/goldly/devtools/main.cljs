


; ; (defmethod reagent-page :demo/main [& args]


#_(defn available-pages []
    (->> (methods reagent-page)
         keys
         (remove #(= :default %))
         (into [])))

(defn devtools []
  [:div
   [:h1.text-xl.text-red-600 "goldly devtools"]

   [link/href "/goldly/about" "goldly developer tools (OLD)"]

   [link/href "/goldly/scratchpad" "scratchpad"]
   [link/href "/goldly/viewer" "notebook viewer"]])

(defn devtools-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
   [devtools]])

(add-page devtools :devtools)


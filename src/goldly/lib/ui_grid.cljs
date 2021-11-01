(defn s-cols [nr]
  (->> (take nr (repeatedly (fn [] "1fr ")))
       (str/join "")))

(defn grid [{:keys [cols background-color]
             :or {cols 2
                  background-color "orange"}} & children]
  (into ^:R [:div {:style {:display :grid
                           :grid-template-columns  (s-cols cols) ; "400px 400px 400px 400px" 
                           :background-color background-color}}]
        children))

(ns notebook.test27.greeter)

(defn greet [{:keys [name]} & args]
  [:div
   [:p "Hello, " 
    [:span {:class "text-bold text-blue-500 text-xl"} name]]])

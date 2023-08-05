(ns notebook.test27.greeter)

(defn greet [{:keys [name]} & args]
  [:div
   [:p "Hello, " name]])

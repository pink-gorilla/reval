(ns demo.greeter)

(defn greet [{:keys [name]}]
  [:p.bg-blue-300 "Hello, "
   [:span.text-big.text-bold name]])
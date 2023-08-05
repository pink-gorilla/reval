(ns notebook.test27.render-demo
  (:require
   [reagent.core :as r]
   [reval.goldly.display :as display]))
[1 2 3]

(defn test [n]
  (+ 1 n))

(test 10)

(resolve 'notebook.test27.render-demo/test)

(display/hiccup
 [:div
  [:h1.text-bold.text-blue-500.text-xl "the world"]
  [:p "is a big and crazy place"]])

(def state (r/atom :init))

(defn state-ui []
  [:div
   [:p "state is: " @state]])

(display/reagent
 [state-ui])

(reset! state :perfect)

; render-fn not found -> has to show error
(display/hiccup
 ['bongistan.superstar/kabumm 42])

(display/reagent
 ['ui.highlightjs/highlightjs "(println 123)"])

(display/reagent
 ['notebook.test27.greeter/greet {:name "Wolfgang"}])

(display/render-fn
 'notebook.test27.greeter/greet {:name "Wolfgang"})

(display/render-fn
 'ui.highlightjs/highlightjs
 "(println 123)")


(display/render-fn
 'notebook.test27.greeter/greet
 {:name "Dschingis Kahn"})

(display/reagent
 ['notebook.test27.greeter/greet
  {:name "Dschingis Kahn"}])

(display/hiccup
 [:h1.text-green-600 "Just Hickup"])















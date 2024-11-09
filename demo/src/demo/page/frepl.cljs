(ns demo.page.frepl
  (:require
   [reval.frepl :refer [show-floating-repl show-floating-repl-namespace]]))

(defn page [{:keys [route-params query-params handler] :as route}]
  [:div
   [:h1 "I am a normal reagent page. But I can add a floating repl."]
   [:a {:on-click #(show-floating-repl {:code "(+ 1 2 3)"})}
    [:p "show code (floating)"]]
   [:a {:on-click #(show-floating-repl {:code "(+ 1 2 3)"
                                        :render-fn 'reval.viz.render-fn/reagent
                                        :data ^{:hiccup true}
                                        [:span {:style {:color "blue"}} "25"]})}
    [:p "show code (eval result)"]]

   [:a {:on-click #(show-floating-repl-namespace {:ns "notebook.study.movies"
                                                  :kernel :clj})}
    [:p "show code (namespace)"]]

   [:a {:on-click #(show-floating-repl-namespace {:ns "demo.notebook.highcharts"
                                                  :kernel :clj})}
    [:p "show code (highcharts)"]]])


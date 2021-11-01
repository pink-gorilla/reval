
(defn h1 [t]
  [:h1.text-xl.text-blue-900.mt-5 t])

(defn devtools []
  [:div.m-5
   [:div.mb-5]
   [h1 "goldly devtools"]
   [:div.mb-5]
   
   [h1 "What is goldly"]

   [:ul
    [:li "Can run clj code in the browser. This is done via sci interpreter."]
    [:li "Via hiccup-fh (functional hiccup) new render functions can be executed from clj."]
    [:li (str "The goldly extension manager will compile your favorite hiccup-fn functions "
              "into a precompiled js bundle that is served with goldly")
           ]]

   [h1 "How to use goldly"]


   
   ])

(defn devtools-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
    [site/main-with-header
     [devtools-menu] 30
     [devtools]]])

(add-page devtools-page :devtools)


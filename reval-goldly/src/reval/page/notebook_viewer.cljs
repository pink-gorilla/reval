;; NOTEBOOK UI

(def show-viewer-debug-ui false) ; true for debugging

;; notebook

(defn notebook-debug [nb]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "notebook debug ui"]
   (pr-str nb)])

(defn notebook [{:keys [meta content] :as nb}]
  (let [{:keys [ns eval-time]} meta]
    [:div.bg-indigo-50.p-2
     [:h1.text-xl.text-blue-800.text-xl.pb-2 ns]
     [:p.pb-2 "evaluated: " eval-time]
     [:hr.h-full.border-solid]
     (into [:div]
           (map segment content))
     (when show-viewer-debug-ui
       [notebook-debug nb])]))

;; APP

(def nb-welcome
  {:meta {:ns "goldly.welcome"}
   :content
   [{:code "(println \"Welcome to Goldly Notebook Viewer \")"
     :hiccup [:h1.text-blue-800 "Welcome to Notebook Viewer!"]
     :out "Welcome to Goldly Notebook Viewer"}]})

(defn viewer-debug [query-params]
  [:div.bg-gray-500.pt-10.hoover-bg-blue-300
   [:p.font-bold "viewer debug ui"]
   [:p "query params:"]
   [:p (pr-str query-params)]
   [:p "ns: " (:ns query-params)]])

(defn viewer [query-params]
  (fn [{:keys [ns fmt]
        :or {fmt :clj}
        :as query-params}]
    (let [fmt (if (string? fmt)
                (keyword fmt)
                fmt)
          c [url-loader {:fmt :clj
                         :url :nb/collections}
             #(notebook-collection :viewer %)]
          nb [url-loader {:fmt :clj
                          :url :nb/load
                          ;:arg-fetch ns
                          :args-fetch [ns fmt]}
              notebook]]
      [:div
       (if (< 500 (.-availWidth js/screen))
         ; big screen
         [layout/sidebar-main
          c
          (if ns
            nb
            [notebook nb-welcome])]
         ; small screen
         (if ns
           nb
           c))

       (when show-viewer-debug-ui
         [viewer-debug query-params])])))

(defn viewer-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
   [viewer query-params]])

;(add-page-template viewer-page :viewer)
(add-page viewer-page :viewer)

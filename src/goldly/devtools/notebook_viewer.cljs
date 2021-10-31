(def welcome-notebook
  {:meta {:ns "goldly.welcome"}
   :content
   [{:src "(println \"Welcome to Goldly Notebook Viewer \")"
     :hiccup [:h1.text-blue-800 "Welcome to Notebook Viewer!"]
     :out "Welcome to Goldly Notebook Viewer"}]})

(def empty-viewer-state
  {:notebooks {:welcome welcome-notebook}
   :current :welcome})

(defonce viewer-state
  (r/atom empty-viewer-state)) ; start empty.

;; view

(def show-viewer-debug-ui false) ; true for debugging

(defn segment-debug [segment]
  [:div.bg-gray-500
   [:p.font-bold "segment debug ui"]
   (pr-str segment)])

(defn segment [{:keys [src out hiccup] :as segment}]
  (let [scode (:code segment)]
    [:div
     (when scode
       [code scode])
     (when (not (str/blank? out))
       [text2 out])
     (when hiccup
       [:div.mt-1.mb-1
        (->hiccup hiccup)])
     (when show-viewer-debug-ui
       [segment-debug segment])]))

(defn notebook-debug [nb]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "notebook debug ui"]
   (pr-str nb)])

(defn notebook [{:keys [meta content] :as nb}]
  (let [{:keys [ns created]} meta]
    [:div.bg-indigo-100.p-2
     [:h1.text-xl.text-blue-800.text-xl.pb-2 ns]
     [:p.pb-2 "evaluated: " created]
     [:hr]
     (into [:div]
           (map segment content))
     (when show-viewer-debug-ui
       [notebook-debug nb])]))

(pinkie/register-tag :p/notebook notebook)

;; DATA

(defn url-nb [ns]
  (str "http://localhost:8000/api/rdocument/file/" ns "/notebook.edn"))

#_(defn get-notebooks-once []
    (when (empty? (get-in @viewer-state [:notebooks :data]))
      (get-edn "/api/notebook/ns" viewer-state [:notebooks])))

;; COLLECTION

(defn nb-chooser [nb-name]
  [:span.m-1.border.p-1
   {:on-click #(swap! viewer-state assoc :current nb-name)}
   nb-name])

(defn notebook-list [notebook-names]
  (into
   [:div]
   (map nb-chooser notebook-names)))

;; APP

(defn viewer-debug []
  [:div.bg-gray-500.pt-10.hoover-bg-blue-300
   [:p.font-bold "viewer debug ui"]
   [:p "viewer state:"]
   [:p (-> @viewer-state pr-str)]])

(defn viewer []
  (fn []
    (let [{:keys [current notebooks]} @viewer-state
          nb (get notebooks current)
          notebook-names (-> @viewer-state :notebooks keys)]
      [:div
       [notebook-list notebook-names]
       [notebook nb]
       (when show-viewer-debug-ui
         [viewer-debug])])))

(defn viewer-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
   [viewer]])

(add-page viewer-page :viewer)
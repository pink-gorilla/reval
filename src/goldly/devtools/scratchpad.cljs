(def empty-scratchpad-hiccup
  [:div ;.bg-blue-500.h-24.pt-3
   [:h1.text-xxl "your scratchpad is empty!"]
   [:p.mb-3 "you can send hiccup to the browser"]
   [code "(goldly.scratchpad/show! [:p \"hello \"])"]])

(def demo-hiccup
  [:p.bg-red-300 "demo123"])

(defonce scratchpad-hiccup
  (r/atom empty-scratchpad-hiccup))

(defonce scratchpad-hiccup-raw
  (r/atom empty-scratchpad-hiccup))

(defn clear-scratchpad [& args]
  (reset! scratchpad-hiccup empty-scratchpad-hiccup)
  (reset! scratchpad-hiccup-raw empty-scratchpad-hiccup))

(defn show-hiccup [h & args]
  (let [h-fn (->hiccup h)]
    (reset! scratchpad-hiccup h-fn)
    (reset! scratchpad-hiccup-raw h)))

(defn scratchpad []
  [:div.ml-5

   ; header
   [:div.pt-5
    [:span.text-xl.text-blue-500.text-bold.mr-4 "scratchpad"]
    [:button.bg-gray-400.m-1 {:on-click clear-scratchpad} "clear"]
    [:button.bg-gray-400.m-1 {:on-click #(show-hiccup demo-hiccup)} "demo"]]
   ;; hiccup (source)
   [:p.text-xl.text-blue-500.mt-3.mb-3 "hiccup"]
   [:div.bg-gray-300  (pr-str @scratchpad-hiccup-raw)]
   ; separator
   [:hr.mt-5]
   ; hiccup
   [:p.text-xl.text-blue-500.mt-3.mb-3 "output"]
   @scratchpad-hiccup])

(defn scratchpad-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
   [scratchpad]])

(add-page scratchpad-page :scratchpad)

; {:op     :show :clear
;  :hiccup [:p "hi"]
;  :ns     demo.playground.cljplot

(defn process-scratchpad-op [{:keys [op hiccup] :as msg}]
  (case op
    :clear (clear-scratchpad)
    :show  (show-hiccup hiccup)
    (println "unknown viewer op:" op)))

(rf/reg-event-fx
 :scratchpad/msg
 (fn [{:keys [db]} [_ msg]]
   (println "scratchpad msg received: " msg)
   (process-scratchpad-op msg)
   nil))


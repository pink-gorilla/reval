(def show-loader-debug-ui false)

; (get-edn "/r/repl/bongo.edn" state [:data])

;http://localhost:8000/api/viewer/file/demo.playground.cljplot/1.txt

(defn load-url [fmt url a]
  (if url
    (when (not (= url (:url @a)))
      (info (str "load-url: " url))
      (swap! a assoc :url url)
      (case fmt
        :txt (http/get-str url a [:data])
        :edn (http/get-edn url a [:data])
        :clj (run-a a [:data] url))
      nil)
    (swap! a assoc :data nil)))

(defn debug-loader [url data args]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "loader debug ui"]
   [:p "url: " url]
   [:p "args: " (pr-str args)]
   [:p "data: " data]])

(defn url-loader [{:keys [url fmt]
                   :or {fmt :txt}}
                  fun args]
  (let [a (r/atom {:data nil
                   :url nil})]
    (fn [{:keys [url fmt]
          :or {fmt :txt}}
         fun args]
      (load-url fmt url a)
      (if-let [d (:data @a)]
        [:div
         [error-boundary
          (if (empty? args)
            (fun d)
            (apply fun d args))]
         (when show-loader-debug-ui
           [debug-loader url d args])]
        [:div "loading: " url]))))
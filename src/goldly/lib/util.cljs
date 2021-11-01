
(defn link-fn [fun text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])


(defn rdoc-link [ns name]
  (str "/api/rdocument/file/" ns "/" name))  




(defn print-result [t]
  (fn [r]
    (println "callback result: " r)))

(defn send-msg [{:keys [type args fn-callback timeout]
                 :or {args []
                      fn-callback (print-result type)
                      timeout 5000}}]
  (rf/dispatch [:ws/send [type args] fn-callback timeout]))




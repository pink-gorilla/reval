; eval

(def demo-code "(* 6 (+ 7 7))")

(defonce scratchpad-code
  (r/atom "(+ 7 7)"))

(defonce cljs-er
  (r/atom nil))

(defonce clj-er
  (r/atom {:er nil}))

(defn eval-cljs []
  (reset! clj-er {:er nil})
  (let [code @scratchpad-code
        _ (println "eval cljs: " code)
        er (compile-sci code)
        er (if-let [result (:result er)]
             (assoc er :hiccup (value->hiccup result))
             er)]
    (println "cljs eval result:" er)
    (reset! cljs-er er)))

(defn eval-clj []
  (let [code @scratchpad-code
        _ (println "eval clj: " code)]
    (reset! clj-er {:er nil})
    (reset! cljs-er nil)
    (run-a clj-er [:er] :viz-eval {:code code})))

(defn repl-header []
  [:div.pt-5
   [:span.text-xl.text-blue-500.text-bold.mr-4 "repl"]
   [:button.bg-gray-400.m-1 {:on-click #(reset! scratchpad-code demo-code)} "demo"]
   [:span "ns: " "user"]
   [:button.bg-gray-400.m-1 {:on-click eval-cljs} "eval cljs"]
   [:button.bg-gray-400.m-1 {:on-click eval-clj} "eval clj"]])

(defn repl-output []
  [:div.w-full.h-full
   [:div#repltarget]
   (when @cljs-er
     [:div
      [:p (pr-str @cljs-er)]
      [render-vizspec2 (:hiccup @cljs-er)]])
   (when-let [er (:er @clj-er)]
     [:div.overflow-scroll.h-full.w-full
      ;[:p (pr-str er)]
      ;[render-vizspec2 (:hiccup er)]
      [segment er]])])

(defn style-codemirror-fullscreen []
  ; height: auto; "400px" "100%"  height: auto;
  ; auto will make the editor resize to fit its content (i
  [:style ".my-codemirror > .CodeMirror { 
              font-family: monospace;
              height: 100%;
              min-height: 100%;
            }"])

(defn repl []
  [spaces/viewport
    ;"top"
   [spaces/top-resizeable {:size 50}
    [repl-header]] ; 
   [spaces/fill {:class "bg-green-200"}
    [:div.w-full.h-full.bg-red-200
     [spaces/left-resizeable {:size "40%"
                              :class "bg-blue-500"}

      [:div.w-full.h-full.bg-white-200
       [style-codemirror-fullscreen]
       [codemirror 27 scratchpad-code]]]

     [spaces/fill {}
      [repl-output]]]]])

(add-page repl :repl)

; {:op     :show :clear
;  :hiccup [:p "hi"]
;  :ns     demo.playground.cljplot

(defn remote-eval [code]
  (println "remote eval: " code)
  (let [eval-result (compile-sci code)]
     ;(rf/dispatch [:goldly/send :scratchpad/evalresult {:code code :result eval-result}])
     ;(run-cb {:fun :scratchpad/evalresult :args {:code code :result eval-result}})
    (send! [:scratchpad/evalresult {:code code :result eval-result}] (fn [& _]) 2000)))

(defn process-repl-op [{:keys [op hiccup code] :as msg}]
  (case op
    ;:clear (clear-scratchpad)
    ;:show  (show-hiccup hiccup)
    :eval (remote-eval code)
    (println "unknown op:" op)))

(rf/reg-event-fx
 :repl/msg
 (fn [{:keys [db]} [_ msg]]
   (println "repl msg received: " msg)
   (process-repl-op msg)
   nil))


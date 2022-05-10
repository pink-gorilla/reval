; eval

(def demo-code "(* 6 (+ 7 7))")

(defonce scratchpad-code
  (r/atom
   "(+ 7 7)
    (defn bad-ui []
      (throw {:type :custom-error}))
    (with-meta 
      [:p.text-blue-600.text-xl.text-bold \"hello\"
        ['user/bongo {:a 3}]]
        ['user/bad-ui]
        {:R true})

    "))

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

(defn repl-header [nbns fmt]
  [:div.pt-5
   [:span.text-xl.text-blue-500.text-bold.mr-4 "repl"]
   [:button.bg-gray-400.m-1 {:on-click #(reset! scratchpad-code demo-code)} "demo"]
   [:span "ns: " nbns "  format: " fmt]
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

(defn editor [ns fmt]
  (let [loaded (r/atom [nil nil])
        id (r/atom 1)]
    (fn [ns fmt]
      (let [comparator [ns fmt]]
        (when (not (= comparator @loaded))
          (println "loaded: " @loaded)
          (reset! loaded comparator)
          (run-cb {:fun :nb/load-src
                   :args [ns (keyword fmt)]
                   :timeout 1000
                   :cb (fn [[s {:keys [result]}]]
                         (println "code: " result)
                         (reset! scratchpad-code result)
                         (swap! id inc))}))
        [:div.w-full.h-full.bg-white-200
         [style-codemirror-fullscreen]
         [codemirror @id scratchpad-code]]))))

(defn repl [url-params]
  (fn [{:keys [query-params]} url-params]
    (let [{:keys [ns fmt]
           :or {fmt "clj"
                ns "user"}} query-params]
      [spaces/viewport
    ;"top"
       [spaces/top-resizeable {:size 50}
        [repl-header ns fmt]] ; 
       [spaces/fill {:class "bg-green-200"}
        [:div.w-full.h-full.bg-red-200

         [spaces/left-resizeable {:size "20%"
                                  :class "bg-blue-500"}
          [url-loader {:fmt :clj
                       :url :nb/collections}
           #(notebook-collection :repl %)]]

         [spaces/left-resizeable {:size "40%"
                                  :class "bg-blue-500"}
          [editor ns fmt]]

         [spaces/fill {}
          [repl-output]]]]])))

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


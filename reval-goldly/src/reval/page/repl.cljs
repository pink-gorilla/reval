; eval

(def demo-code "(* 6 (+ 7 7))")

(defonce repl-code
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

(defonce editor-id (r/atom 1))

(defn cm-get-code []
  (-> (cm/get @editor-id)
      (cm/get-code)))

(defn cm-set-code [code]
  (let [c (cm/get @editor-id)]
    (cm/set-code c code)
    (cm/focus c)))

(def cm-opts {:lineWrapping false})

(defn style-codemirror-fullscreen []
  ; height: auto; "400px" "100%"  height: auto;
  ; auto will make the editor resize to fit its content (i
  [:style ".my-codemirror > .CodeMirror { 
              font-family: monospace;
              height: 100%;
              min-height: 100%;
              max-height: 100%;
            }"])

;(defn cm-editor-atom []
;  [:div.w-full.h-full.bg-white-200
;    [style-codemirror-fullscreen]
;      [user/codemirror @editor-id repl-code]])

(defn cm-editor []
  [:<> [style-codemirror-fullscreen] ;cm/style-inline
   [:div.my-codemirror.w-full.h-full
    [user/codemirror-unbound @editor-id cm-opts]]])

;; results

(defonce cljs-er
  (r/atom nil))

(defonce clj-er
  (r/atom {:er nil}))

(defonce nb-er
  (r/atom {:nb nil}))

(defn clear []
  (reset! clj-er {:er nil})
  (reset! cljs-er nil)
  (reset! nb-er {:nb nil}))

;; eval cljs

(defn eval-cljs []
  (clear)
  (let [code @repl-code
        _ (println "eval cljs: " code)
        er (compile-sci code)
        er (if-let [result (:result er)]
             (assoc er :hiccup (value->hiccup result))
             er)]
    (println "cljs eval result:" er)
    (reset! cljs-er er)))

(defn eval-clj []
  (clear)
  (let [code @repl-code
        _ (println "eval clj: " code)]
    (run-a clj-er [:er] :viz-eval {:code code})))

;nb-eval

(defn eval-nb [ns fmt]
  (clear)
  (let [fmt (keyword fmt) ;:clj
        ;ns "demo.notebook.abc"
        _  (println "format: " fmt " ns: " ns)
        code @repl-code
        _ (println "eval clj: " code)]
    (run-a nb-er [:nb] :nb/eval ns))) ;fmt

;; EDITOR 

(defn current-expression []
  (let [id @editor-id]
    (when-let [c (cm/get id)]
      (let [p (cm/cursor-position c)
            {:keys [line col]} p
            code (cm-get-code)
            ;code "(+ 3 1)\n(* 3 4 5 \n   6 7)\n(println 55)"
            cur-exp (block-for code [line col])
            code-exp (second cur-exp)]
        ;cur-exp
        code-exp))))

(defn print-position []
  (when-let [code-exp (current-expression)]
    (info code-exp)))

(defn eval-clj-segment [ns]
  (clear)
  (when-let [code (current-expression)]
    (println "eval clj segment: " code)
    (run-a clj-er [:er] :viz-eval {:code code :ns ns})))

(def cur-ns (r/atom "ns"))

(rf/reg-event-fx
 :repl/eval-expression
 (fn [cofx [_ data]]
   (info (str "evaluating repl segment!" data))
   ;(print-position)
   (eval-clj-segment @cur-ns)
   nil))

;; HEADER

(defn repl-header [nbns fmt]
  (reset! cur-ns nbns)
  [:div.pt-5
   [:span.text-xl.text-blue-500.text-bold.mr-4 "repl"]
   [:button.bg-gray-400.m-1 {:on-click #(reset! repl-code demo-code)} "demo"]
   [:span "ns: " nbns "  format: " fmt]
   [:button.bg-gray-400.m-1 {:on-click clear} "clear output"]
   [:button.bg-gray-400.m-1 {:on-click eval-cljs} "eval cljs"]
   [:button.bg-gray-400.m-1 {:on-click eval-clj} "eval clj"]
   [:button.bg-gray-400.m-1 {:on-click #(eval-nb nbns fmt)} "nb eval"]
   [:button.bg-gray-400.m-1 {:on-click #(eval-clj-segment nbns)} "eval cur expression"]
   [:button.bg-red-400.m-1 #_{:on-click eval-clj} "send to pages"]
   [:button.bg-red-400.m-1 #_{:on-click eval-clj} "save"]])

(defn repl-output []
  [:div.w-full.h-full.bg-gray-100
   [:div#repltarget]
   [:div.overflow-scroll.h-full.w-full
    (when @cljs-er
      (if-let [err (get-in @cljs-er [:error :err])]
        [:p.text-red-500 err]
        #_[:p (pr-str @cljs-er)]
        [render-vizspec2 (:hiccup @cljs-er)]))
    (when-let [er (:er @clj-er)]
        ;[:p (pr-str er)]
        ;[render-vizspec2 (:hiccup er)]
      [segment er])
    (when-let [nb (:nb @nb-er)]
        ;[:p (pr-str er)]
        ;[render-vizspec2 (:hiccup er)]
        ;[segment er]
        ;(pr-str nb)
      [notebook nb])]])

(defn editor [ns fmt]
  (let [loaded (r/atom [nil nil])
        ;id (r/atom 1)
        ]
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
                         (reset! repl-code result)
                         (cm-set-code result)
                         ;(swap! editor-id inc)
                         )}))
        [cm-editor]))))

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

         [spaces/left-resizeable {:size "10%"
                                  :class "bg-gray-100 max-h-full overflow-y-auto"}
          [url-loader {:fmt :clj
                       :url :nb/collections}
           #(notebook-collection :repl %)]]

         [spaces/left-resizeable {:size "40%"
                                  :class "bg-gray-100"}
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


(ns reval.goldly.page.repl
  (:require
   [r]
   [cm]
   [user]
   [layout]
   [reval.goldly.url-loader]
   [reval.goldly.vizspec]
   [reval.goldly.notebook.collection]
   [reval.goldly.notebook.clj-result]))

; 
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

(defonce cur-ns (r/atom "user"))

(defonce editor-id (r/atom 1))

(defn cm-get-code []
  (-> (cm/get @editor-id)
      (cm/get-code)))

(defn cm-set-code [code]
  (let [c (cm/get @editor-id)]
    (cm/set-code c code)
    (cm/focus c)))

(defn save-code [path]
  (let [code (cm-get-code)]
    (user/run-cb {:fun :nb/save-code
             :args [{:code code :path path}]
             :timeout 1000
             :cb (fn [[s {:keys [result]}]]
                   ;(println "result: " result)
                   )})))

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

#_(defn eval-cljs []
    (clear)
    (let [code (cm-get-code)
          _ (println "eval cljs: " code)
          er (user/compile-sci code)
          er (if-let [result (:result er)]
               (assoc er :hiccup (value->hiccup result))
               er)]
      ;(println "cljs eval result:" er)
      (reset! cljs-er er)))

(defn eval-cljs [{:keys [code ns]}]
  (let [er-p (user/compile-sci-async code)]
    (.catch er-p (fn [err]
                   ;(println "eval failed: " err)
                   ))
    (.then er-p
           (fn [er]
             ;(println "cljs er: " er)
             (when [er]
               ;(println "cljs eval result:" er)
               (let [er-h {:hiccup (user/value->hiccup er)}]
                 (reset! cljs-er er-h))
         ;(reset! cur-ns (:ns er))
               )))))

(defn eval-clj [opts]
   ;(run-a clj-er [:er] :viz-eval opts)
  (user/run-cb {:fun :viz-eval
           :args [opts]
           :timeout 1000
           :cb (fn [[s {:keys [result]}]]
                 (let [{:keys [ns]} result]
                   ;(println "clj-eval result: " result)
                   (reset! clj-er {:er result})
                   ;(println "setting ns to: " ns)
                   (reset! cur-ns ns)))}))

(defn eval-all [fmt]
  (clear)
  (let [code (cm-get-code)
        opts {:code code :ns nil}]
    (case fmt
      "cljs" (eval-cljs opts)
      "clj" (eval-clj opts)
      ;(info (str "can not eval. format unknown: " fmt))
      )))

;nb-eval

(defn eval-nb [ns fmt]
  (clear)
  (let [fmt (keyword fmt) ;:clj
        ;ns "demo.notebook.abc"
        ;_  (println "format: " fmt " ns: " ns)
        code (cm-get-code)
        ;_ (println "eval clj: " code)
        ]
    (user/run-a nb-er [:nb] :nb/eval ns))) ;fmt

;; EDITOR 

(defn current-expression []
  (let [id @editor-id]
    (when-let [c (cm/get id)]
      (let [p (cm/cursor-position c)
            {:keys [line col]} p
            code (cm-get-code)
            ;code "(+ 3 1)\n(* 3 4 5 \n   6 7)\n(println 55)"
            cur-exp (user/block-for code [line col])
            code-exp (second cur-exp)]
        ;cur-exp
        code-exp))))

(defn print-position []
  (when-let [code-exp (current-expression)]
    ;(info code-exp)
    ))

(defn eval-segment [fmt]
  (clear)
  (when-let [code (current-expression)]
    (let [opts {:code code :ns @cur-ns}]
      ;(println "eval segment: " code)
      (case fmt
        "cljs" (eval-cljs opts)
        "clj" (eval-clj opts)
        ;(info (str "can not eval. format unknown: " fmt))
        ))))

(def cur-ns (r/atom "ns"))
(def cur-fmt (r/atom "fmt"))

(rf/reg-event-fx
 :repl/eval-expression
 (fn [cofx [_ data]]
   ;(info (str "evaluating repl segment!" data))
   ;(print-position)
   (eval-segment @cur-fmt)
   nil))

;; HEADER

(def cur-path (r/atom nil))

(defn repl-header [nbns fmt path]
  (reset! cur-ns nbns)
  (reset! cur-fmt fmt)
  (reset! cur-path path)
  [:div.pt-5
   [:span.text-xl.text-blue-500.text-bold.mr-4 "repl"]
   [:button.bg-gray-400.m-1 {:on-click #(reset! repl-code demo-code)} "demo"]
   [:span "ns: " nbns "  format: " fmt]
   ;[:button.bg-gray-400.m-1 {:on-click eval-cljs} "eval cljs"]
   ;[:button.bg-gray-400.m-1 {:on-click eval-clj} "eval clj"]
   [:button.bg-gray-400.m-1 {:on-click #(eval-all fmt)} "eval all"]
   [:button.bg-gray-400.m-1 {:on-click #(eval-segment fmt)} "eval current"]
   [:button.bg-gray-400.m-1 {:on-click #(eval-nb nbns fmt)} "nb eval"]
   [:button.bg-gray-400.m-1 {:on-click #(save-code path)} "save"]
   [:div.bg-blue-300.inline-block
    ; output
    [:button.bg-gray-400.m-1 {:on-click clear} "clear output"]
    [:button.bg-red-400.m-1 #_{:on-click eval-clj} "send to pages"]]])

(defn repl-output []
  [:div.w-full.h-full.bg-gray-100
   [:div#repltarget]
   [:div.overflow-scroll.h-full.w-full
    (when @cljs-er
      (if-let [err (get-in @cljs-er [:error :err])]
        [:p.text-red-500 err]
        #_[:p (pr-str @cljs-er)]
        [reval.goldly.vizspec/render-vizspec2 (:hiccup @cljs-er)]))
    (when-let [er (:er @clj-er)]
        ;[:p (pr-str er)]
        ;[reval.goldly.vizspec/render-vizspec2 (:hiccup er)]
      [reval.goldly.notebook.clj-result/segment er])
    (when-let [nb (:nb @nb-er)]
        ;[:p (pr-str er)]
        ;[reval.goldly.vizspec/render-vizspec2 (:hiccup er)]
        ;[segment er]
        ;(pr-str nb)
      [reval.goldly.notebook.clj-result/notebook nb])]])

(defn editor [ns fmt path]
  (let [loaded (r/atom [nil nil])
        ;id (r/atom 1)
        ]
    (fn [ns fmt path]
      (let [comparator [ns fmt path]]
        (when (not (= comparator @loaded))
          ;(println "loaded: " @loaded)
          (reset! loaded comparator)
          (user/run-cb {:fun :nb/load-src
                   :args [ns (keyword fmt)]
                   :timeout 1000
                   :cb (fn [[s {:keys [result]}]]
                         ;(println "code: " result)
                         (reset! repl-code result)
                         (cm-set-code result)
                         ;(swap! editor-id inc)
                         )}))
        [cm-editor]))))

(defn repl [url-params]
  (fn [{:keys [query-params]} url-params]
    (let [{:keys [ns fmt path]
           :or {fmt "clj"
                ns "user"}} query-params]
      [spaces/viewport
    ;"top"
       [spaces/top-resizeable {:size 50}
        [repl-header ns fmt path]] ; 
       [spaces/fill {:class "bg-green-200"}
        [:div.w-full.h-full.bg-red-200

         [spaces/left-resizeable {:size "10%"
                                  :class "bg-gray-100 max-h-full overflow-y-auto"}
          [reval.goldly.url-loader/url-loader {:fmt :clj
                       :url :nb/collections}
           #(reval.goldly.notebook.collection/notebook-collection :repl %)]]

         [spaces/left-resizeable {:size "40%"
                                  :class "bg-gray-100"}
          [editor ns fmt path]]

         [spaces/fill {}
          [repl-output]]]]])))

(user/add-page repl :repl)

; {:op     :show :clear
;  :hiccup [:p "hi"]
;  :ns     demo.playground.cljplot

(defn remote-eval [code]
  ;(println "remote eval: " code)
  (let [eval-result (user/compile-sci code)]
     ;(rf/dispatch [:goldly/send :scratchpad/evalresult {:code code :result eval-result}])
     ;(run-cb {:fun :scratchpad/evalresult :args {:code code :result eval-result}})
    (user/send! [:scratchpad/evalresult {:code code :result eval-result}] (fn [& _]) 2000)))

(defn process-repl-op [{:keys [op hiccup code] :as msg}]
  (case op
    ;:clear (clear-scratchpad)
    ;:show  (show-hiccup hiccup)
    :eval (remote-eval code)
    ;(println "unknown op:" op)
    ))

(rf/reg-event-fx
 :repl/msg
 (fn [{:keys [db]} [_ msg]]
   ;(println "repl msg received: " msg)
   (process-repl-op msg)
   nil))


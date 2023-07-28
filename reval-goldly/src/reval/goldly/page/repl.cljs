(ns reval.goldly.page.repl
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [cm]
   [ui.codemirror :refer [codemirror-unbound]]
   [layout]
   [spaces]
   [modular.ws.core :as ws]
   [goldly :refer [error-view]]
   [goldly.sci :refer [compile-sci-async compile-sci]]
   [goldly.service.core :as service]
   [goldly.page :as page]
   [reval :refer [value->hiccup block-for]]
   [reval.goldly.url-loader :refer [url-loader]]
   [reval.goldly.vizspec :refer [render-vizspec2]]
   [reval.goldly.notebook-ui.collection :refer [notebook-collection]]
   [reval.goldly.notebook-ui.clj-result :refer [segment notebook]]))

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
    (service/run-cb {:fun 'reval.services/save-code
                     :args [{:code code :path path}]
                     :timeout 1000
                     :cb (fn [[_s {:keys [_result]}]]
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
;      [ui.codemirror/codemirror @editor-id repl-code]])

(defn cm-editor []
  [:<> [style-codemirror-fullscreen] ;cm/style-inline
   [:div.my-codemirror.w-full.h-full
    [codemirror-unbound @editor-id cm-opts]]])

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
          er (goldly.sci/compile-sci code)
          er (if-let [result (:result er)]
               (assoc er :hiccup (value->hiccup result))
               er)]
      ;(println "cljs eval result:" er)
      (reset! cljs-er er)))

(defn eval-cljs [{:keys [code _ns]}]
  (let [er-p (compile-sci-async code)]
    (-> er-p
        (.then
         (fn [er]
           (when [er] ; {:id :code :value :err :out :ns}
             (let [er-h (assoc er :hiccup (value->hiccup (:value er)))]
               (reset! cljs-er er-h)))))
        (.catch (fn [e]
                   ;(println "eval failed: " err)
                  (when-let [sci-err (goldly/exception->error e)]
                    (reset! cljs-er sci-err)))))))

(defn eval-clj [opts]
   ;(run-a clj-er [:er] :viz-eval opts)
  (service/run-cb {:fun 'reval.services/viz-eval
                   :args [opts]
                   :timeout 60000
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

(defn eval-nb [ns _fmt]
  (clear)
  (let [;fmt (keyword fmt) ;:clj
        ;ns "demo.notebook.abc"
        ;_  (println "format: " fmt " ns: " ns)
        ;code (cm-get-code)
        ;_ (println "eval clj: " code)
        ]
    (service/run-a nb-er [:nb] 'reval.document.notebook/eval-notebook ns))) ;fmt

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
  (when-let [_code-exp (current-expression)]
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
(def cur-fmt (r/atom "fmt"))

(rf/reg-event-fx
 :repl/eval-expression
 (fn [_cofx [_ _data]]
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
      (if (:err @cljs-er)
        [error-view @cur-ns @cljs-er] ;[:p.text-red-500 err]
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

(defn editor [_ns _fmt _path]
  (let [loaded (r/atom [nil nil])
        ;id (r/atom 1)
        ]
    (fn [ns fmt path]
      (let [comparator [ns fmt path]]
        (when (not (= comparator @loaded))
          ;(println "loaded: " @loaded)
          (reset! loaded comparator)
          (service/run-cb {:fun 'reval.document.notebook/load-src
                           :args [ns (keyword fmt)]
                           :timeout 1000
                           :cb (fn [[s {:keys [result]}]]
                         ;(println "code: " result)
                                 (reset! repl-code result)
                                 (cm-set-code result)
                         ;(swap! editor-id inc)
                                 )}))
        [cm-editor]))))

(defn repl [_url-params]
  (fn [{:keys [query-params]}]
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
          [url-loader {:fmt :clj
                       :url 'reval.services/nb-collections
                       :args []}
           #(notebook-collection :repl %)]]

         [spaces/left-resizeable {:size "60%"
                                  :class "bg-gray-100"}
          [editor ns fmt path]]

         [spaces/fill {}
          [repl-output]]]]])))

(page/add repl :repl)

; {:op     :show :clear
;  :hiccup [:p "hi"]
;  :ns     demo.playground.cljplot

(defn remote-eval [code]
  ;(println "remote eval: " code)
  (let [eval-result (compile-sci code)]
     ;(rf/dispatch [:goldly/send :scratchpad/evalresult {:code code :result eval-result}])
     ;(run-cb {:fun :scratchpad/evalresult :args {:code code :result eval-result}})
    (ws/send! [:scratchpad/evalresult {:code code :result eval-result}] (fn [& _]) 2000)))

(defn process-repl-op [{:keys [op _hiccup code] :as _msg}]
  (case op
    ;:clear (clear-scratchpad)
    ;:show  (show-hiccup hiccup)
    :eval (remote-eval code)
    ;(println "unknown op:" op)
    ))

(rf/reg-event-fx
 :repl/msg
 (fn [{:keys [_db]} [_ msg]]
   ;(println "repl msg received: " msg)
   (process-repl-op msg)
   nil))


(ns reval.page.repl
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [layout]
   [spaces.core]
   [goldly.service.core :as service]
   [reval.helper.url-loader :refer [url-loader]]
   [reval.notebook-ui.collection :refer [notebook-collection]]
   [reval.notebook-ui.clj-result :refer [segment notebook add-segment empty-notebook]]
   [reval.notebook-ui.editor :as editor]
   [reval.notebook-ui.eval :as eval]))

(defonce repl-code (r/atom ""))

;; results

(defonce clj-er
  (r/atom {:er nil}))

(defonce nb-er
  (r/atom {:nb nil})
  (r/atom {:nb (empty-notebook)}))

(defn clear []
  (reset! clj-er nil)
  ;(reset! nb-er {:nb nil})
  (reset! nb-er {:nb (empty-notebook)}))

;; eval cljs

(defn on-evalresult [er]
  (.log js/console "eval result: " (pr-str er))
  ;(let [x (-> er :out js->clj first)]
  ;  (.log js/console "out2: " x))
  ;(reset! clj-er er)
  (swap! nb-er update :nb add-segment er))

(defn eval-all [fmt]
  (clear)
  (let [code (editor/cm-get-code)
        opts {:code code :ns nil}]
    (case fmt
      "cljs" (eval/eval-cljs on-evalresult opts)
      "clj" (eval/eval-clj on-evalresult opts)
      ;(info (str "can not eval. format unknown: " fmt))
      )))

(defn eval-segment [fmt]
  ;(clear)
  (when-let [code (editor/current-expression)]
    (let [opts {:code code}] ; :ns @cur-ns
      ;(println "eval segment: " code)
      (case fmt
        "cljs" (eval/eval-cljs on-evalresult opts)
        "clj" (eval/eval-clj on-evalresult opts)
        ;(info (str "can not eval. format unknown: " fmt))
        ))))

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
  (reset! eval/cur-ns nbns)
  (reset! cur-fmt fmt)
  (reset! cur-path path)
  [:div.pt-5
   [:span.text-xl.text-blue-500.text-bold.mr-4 "repl"]
   [:span "ns: " nbns "  format: " fmt]
   [:button.bg-gray-400.m-1 {:on-click #(eval-all fmt)} "eval all"]
   [:button.bg-gray-400.m-1 {:on-click #(eval-segment fmt)} "eval current"]
   [:button.bg-gray-400.m-1 {:on-click #(eval-nb nbns fmt)} "nb eval"]
   [:button.bg-gray-400.m-1 {:on-click #(editor/save-code path)} "save"]
   [:div.bg-blue-300.inline-block
    ; output
    [:button.bg-gray-400.m-1 {:on-click clear} "clear output"]
    [:button.bg-red-400.m-1 #_{:on-click eval-clj} "send to pages"]]])

(defn repl-output []
  [:div.w-full.h-full.bg-gray-100
   [:div#repltarget]
   [:div.overflow-scroll.h-full.w-full
    (when-let [er @clj-er]
      [segment er])
    (when-let [nb (:nb @nb-er)]
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
                                 (editor/cm-set-code result)
                         ;(swap! editor-id inc)
                                 )}))
        [editor/cm-editor]))))

(defn repl [_url-params]
  (fn [{:keys [query-params]}]
    (let [{:keys [ns fmt path]
           :or {fmt "clj"
                ns "user"}} query-params]
      [spaces.core
    ;"top"
       [spaces.core {:size 50}
        [repl-header ns fmt path]] ; 
       [spaces.core {:class "bg-green-200"}
        [:div.w-full.h-full.bg-red-200

         [spaces.core {:size "10%"
                                  :class "bg-gray-100 max-h-full overflow-y-auto"}
          [url-loader {:fmt :clj
                       :url 'reval.document.collection/nb-collections
                       :args []}
           #(notebook-collection 'reval.goldly.page.repl/repl %)]]

         [spaces.core/left-resizeable {:size "60%"
                                  :class "bg-gray-100"}
          [editor ns fmt path]]

         [spaces.core/fill {}
          [repl-output]]]]])))



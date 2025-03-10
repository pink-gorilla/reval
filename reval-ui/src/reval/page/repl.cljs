(ns reval.page.repl
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [promesa.core :as p]
   [spaces.core]
   [goldly.service.core :refer [clj]]
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.kernel.clj-remote] ; side effects
   [reval.dali.viewer.collection-viewer :refer [collection-viewer]]
   [reval.dali.viewer.notebook :refer [notebook add-segment empty-notebook]]
   [reval.notebook-ui.editor :as cme]))

(defonce repl-code (r/atom ""))
(defonce cur-fmt (r/atom :clj))
(defonce cur-path (r/atom ""))

(defonce nb-a
  (r/atom (empty-notebook)))

(defn clear []
  (reset! nb-a (empty-notebook)))

;; eval cljs

(defn eval-code [fmt code]
  (let [opts {:code code
              :kernel (keyword fmt)}
        rp (kernel-eval opts)] ; :ns @cur-ns
    (println "eval segment: " opts)
    (p/then rp (fn [er]
                 (.log js/console "eval result: " (pr-str er))
                 (swap! nb-a add-segment er)))))

(defn eval-all [fmt]
  (clear)
  (let [code (cme/cm-get-code)]
    (eval-code fmt code)))

(defn eval-segment [fmt]
  (when-let [code (cme/current-expression)]
    (eval-code fmt code)))

;nb-eval

(defn eval-nb [ns _fmt]
  (clear)
  (let [rp (clj 'reval.document.notebook/eval-notebook ns)]
    (p/then rp (fn [r]
                 (println "notebook eval result: " r)
                 (reset! nb-a (:data r))))))

(rf/reg-event-fx
 :repl/eval-expression
 (fn [_cofx [_ _data]]
   ;(info (str "evaluating repl segment!" data))
   ;(print-position)
   (eval-segment @cur-fmt)
   nil))

;; HEADER

(defn repl-header [nbns fmt path]
  ;(reset! eval/cur-ns nbns)
  (reset! cur-fmt fmt)
  (reset! cur-path path)
  [:div.pt-5
   [:span.text-xl.text-blue-500.text-bold.mr-4 "repl"]
   [:span "ns: " nbns "  format: " fmt]
   [:button.bg-gray-400.m-1 {:on-click #(eval-all fmt)} "eval all"]
   [:button.bg-gray-400.m-1 {:on-click #(eval-segment fmt)} "eval current"]
   [:button.bg-gray-400.m-1 {:on-click #(eval-nb nbns fmt)} "nb eval"]
   [:button.bg-gray-400.m-1 {:on-click #(cme/save-code path)} "save"]
   [:div.bg-blue-300.inline-block
    ; output
    [:button.bg-gray-400.m-1 {:on-click clear} "clear output"]
    [:button.bg-red-400.m-1 #_{:on-click eval-clj} "send to pages"]]])

(defn repl-output []
  [:div.w-full.h-full.bg-gray-500
   [:div#repltarget]
   [:div.overflow-scroll.h-full.w-full
    [notebook @nb-a]]])

(defn editor [_ns _fmt _path]
  (let [loaded (r/atom [nil nil nil])]
    (fn [nbns fmt path]
      (let [comparator [nbns fmt path]]
        (when (not (= comparator @loaded))
          (println "loaded: " @loaded)
          (reset! loaded comparator)
          (-> (clj {:timeout 1000}
                   'reval.document.notebook/load-src
                   nbns (keyword fmt))
              (p/then (fn [result]
                        (println "code result: " result)
                        (reset! repl-code result)
                        (cme/cm-set-code result)
                        ;(swap! editor-id inc)
                        ))))
        [cme/cm-editor]))))

(defn goto-nb [nbinfo]
  (rf/dispatch [:bidi/goto 'reval.page.repl/repl-page
                :query-params nbinfo]))

(defn repl [opts]
  (fn [{:keys [nbns ext path]
        :or {ext "clj"
             nbns "user"
             path ""}}]
    [spaces.core/viewport
     [spaces.core/top-resizeable {:size "10%"
                                  :scrollable false
                                  :class "bg-gray-100"} ; max-h-full overflow-y-auto
      [repl-header nbns ext path]]
     [spaces.core/fill {}
      [spaces.core/left-resizeable {:size "10%"
                                    :scrollable true
                                    :class "bg-gray-100 max-h-full overflow-y-auto"}
       [collection-viewer
        {:link goto-nb}]]
      [spaces.core/fill {}
       [editor nbns ext path]] ; [:div.h-full.w-full.bg-blue-900.max-h-full.overflow-y-auto]
      [spaces.core/right-resizeable {:size "50%"
                                     :scrollable true
                                     :class "bg-blue-100 max-h-full overflow-y-auto"}
       [repl-output]]]]))

(defn repl-page [{:keys [_route-params query-params _handler] :as _route}]
  [repl query-params])

(ns reval.frepl
  (:require
   [taoensso.timbre :refer-macros [debugf info warn warnf error]]
   [reagent.core :as r]
   [promesa.core :as p]
   [ui.overlay :refer [overlay-add overlay-remove]]
   [ui.rnd :refer [rnd]]
   [nano-id.core :refer [nano-id]]
   [clojure.string :refer [blank?]]
  ; codemirror
   [ui.codemirror.api :as api]
   [ui.codemirror.codemirror :refer [codemirror get-editor]]
  ; kernel
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.kernel.clj-remote] ; side effects
   [reval.viz.show :refer [show-data]]
   [reval.helper.ui-helper :refer [text2]]
   [reval.notebook-ui.clj-result :refer [evalerr]]
   ; clj service
   [goldly.service.core :refer [clj]]))

;; codemirror

(defn cm-get-code [editor-id]
  (-> (get-editor editor-id)
      (api/get-code)))

(defn cm-set-code [editor-id code]
  (let [c (get-editor editor-id)]
    (api/set-code c code)
    (api/focus c)))

(def cm-opts {:lineWrapping false})

(defn cm-editor [editor-id]
  [:<>
   ;[theme/style-codemirror-fullscreen] ; fullscreen is not correct name, 100% width/height is better name.
   ])

;; eval cljs

(defn eval-code [id opts r-a]
  (let [code (cm-get-code id)
        opts (assoc opts :code code)
        rp (kernel-eval opts)] ; :ns @cur-ns
    (info "eval code: " opts)
    (-> rp
        (p/then (fn [er]
                  (info "eval code result: " (pr-str er))
                  (reset! r-a er)))
        (p/catch (fn [err]
                   (info "eval code error: " (pr-str err)))))))

(defn show-data-extended [{:keys [_id _ns _src err err-sci out data render-fn] :as segment}]
  (let [scode (:code segment)]
    [:div.flex.flex-col
     ;(pr-str segment)
     ;(when scode [highlightjs scode])
     (when err
       [evalerr err])
     #_(when err-sci
         [evalerr-sci err-sci])
     (when (not (blank? out))
       [:div.bg-blue-200.max-w-full.overflow-x-auto
        [text2 out]])
     (when render-fn
       [:div.mt-1.mb-1
        [show-data render-fn data]])]))

(defn floating-window [id {:keys [kernel ns render-fn data]
                           :or {kernel :clj}}]
  (let [data-a (r/atom {:data data :render-fn render-fn})
        opts {:ns ns :kernel kernel}
        show-data-a (r/atom false)
        eval-to-result (fn []
                         (let [rp (eval-code id opts data-a)]
                           (p/then rp (fn [res]
                                        (reset! show-data-a true)))))]
    (fn [id _ _]
      (let [{:keys [data render-fn err out]} @data-a]
        [:div {:style {:display "grid"
                       :grid-template-rows "34px 1fr"
                       :width "100%"
                       :height "100%"}}
         [:style ".my-codemirror > .CodeMirror { 
                                              font-family: monospace;
                                              height: 100%;
                                              min-height: 100%;
                                              max-height: 100%;
                                            }"]
         [:style ".toolbar-item {
                               padding: 1px;
                               cursor: pointer;
                               margin-right: 5px;
                               border-radius: 3px;
                               transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);}
                      .toolbar-item:hover {
                                background: rgb(228, 228, 228);
                                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
                               }"]

         ;; TOOLBAR MENU
         [:div {:display "flex"
                :width "100%"
                :flexdirection "column"
                :justifycontent "space-between"
                :class "bg-gray-300"}
          [:span.toolbar-item
           {:on-click #(swap! show-data-a not)}
           (if @show-data-a "code" "result")]
          (when-not @show-data-a
            [:button.bg-gray-400.m-1.p-1.toolbar-item {:on-click #(eval-to-result)
                                                                 ;#(eval-code id opts data-a)
                                                       }"eval"])
          ;[:span.toolbar-item "menu"]
          [:button
           {:class "bg-gray-400 m-1 p-1 toolbar-item"
            :style {;:margin-left "auto" ; align one flex child to the right
                    :float "right"}
            :on-click #(overlay-remove id)} "x"]]
         ;; CODEMIRROR
         ;[cm-editor id]
         [:div.my-codemirror.w-full.h-full {:style {:max-height "100%"
                                                    :max-width "100%"
                                                    :height "100%"
                                                    :width "100%"
                                                    :display (when @show-data-a "none")}}
          [codemirror id cm-opts]]
         ;; RESULT
         [:div {:style {:overflow "hidden"
                        :box-shadow "0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23)"
                        :max-height "100%"
                        :max-width "100%"
                        :height "100%"
                        :width "100%"
                        :display (when-not @show-data-a "none")}}
          (cond
            (or (and data render-fn)
                out
                err)
            ;[show-data render-fn data]
            [show-data-extended @data-a]
            render-fn [:div "no viz data"]
            :else [:div "no render-fn"])]]))))

(defn show-floating-repl [{:keys [kernel code ns data render-fn]
                           :or {kernel :clj
                                code ""} :as opts}]
  (let [id (str (nano-id 5))]
    (overlay-add id [rnd {:bounds "window"
                          :default {:width 200
                                    :height 400
                                    :x 50
                                    :y 60}
                          :style {:display "flex"
                                  ;:alignItems "center"
                                  :justifyContent "center"
                                  :border "solid 2px #ddd"
                                  :background "#f0f0f0"}}
                     [floating-window id opts]])
    (cm-set-code id code)))

(defn show-floating-repl-namespace
  [{:keys [kernel ns data render-fn]
    :as opts}]
  (info "loading namespace source: " kernel ns)
  (-> (clj {:timeout 1000} 'reval.document.notebook/load-src ns kernel)
      (p/then (fn [code]
                (info "source loaded: " code)
                (show-floating-repl {:kernel kernel
                                     :code code
                                     :ns ns})))
      (p/catch (fn [err]
                 (error "could not load ns: " ns kernel " error: " err)))))
(ns reval.frepl
  (:require
   [taoensso.timbre :refer-macros [debugf info warn warnf error]]
   [clojure.string :refer [blank?]]
   [reagent.core :as r]
   [promesa.core :as p]
   [nano-id.core :refer [nano-id]]
   [ui.overlay :refer [overlay-add overlay-remove]]
   [ui.rnd :refer [rnd]]
   ; codemirror
   [ui.codemirror.api :as api]
   [ui.codemirror.codemirror :refer [codemirror get-editor]]
   ; clj service
   [goldly.service.core :refer [clj]]
   ; kernel
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.kernel.clj-remote] ; side effects
   [dali.viewer :refer [viewer2]]
   [dali.viewer.text :refer [text]]))

(defn nil-result? [result]
  (let [data (:data result)]
    (= data [:span {:style {:color "grey"}} "nil"])))

(defn segment [{:keys [_id  err out result] :as segment}]
  ; copied and modified from ; [reval.dali.viewer.notebook :refer [segment]]
  ; reason: frepl should have big view of the resulting data, and in a 
  ; notebook the layout is different.
  (println "frepl segment: " segment)
  (cond
    (and result (not (nil-result? result)))
    [:div.mt-1.mb-1.w-full.h-full
     {:style {:max-width "800px"
              :max-height "400px"}}
     [viewer2 result]]
    err
    [:div.mt-1.mb-1.w-full.h-full
     {:style {:max-width "800px"
              :max-height "400px"}}
     [viewer2 err]]
    (not (blank? out))
    [text {:text out
           :class "bg-blue-200 max-w-full overflow-x-auto h-full w-full"}]
    :else
    [:div "no result/error/console output."]))

;; codemirror

(defn cm-get-code [editor-id]
  (-> (get-editor editor-id)
      (api/get-code)))

(defn cm-set-code [editor-id code]
  (let [c (get-editor editor-id)]
    (api/set-code c code)
    (api/focus c)))

(def cm-opts {:lineWrapping false})

(defn cm-editor [editor-id show-data-a]

   ;[theme/style-codemirror-fullscreen] ; fullscreen is not correct name, 100% width/height is better name.
  [:div {:style {:max-height "100%"
                 :max-width "100%"
                 :min-height "100%"
                 :min-width "100%"
                 :height "100%"
                 :width "100%"
                 :display (when @show-data-a "none")}}
   [codemirror editor-id cm-opts]])

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

(defn menu [id opts data-a show-data-a]
  (let [eval-to-result (fn []
                         (let [rp (eval-code id opts data-a)]
                           (p/then rp (fn [res]
                                        (reset! show-data-a true)))))]

    [:div {:display "flex"
           :width "100%"
           :flexdirection "column"
           :justifycontent "space-between"
           :class "bg-gray-300"}
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
     [:style ".CodeMirror { 
                   font-family: monospace;
                   height: 100%;
                   min-height: 100%;
                   max-height: 100%;
                   width: 100%;
                 }"]

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
       :on-click #(overlay-remove id)} "x"]]))

(defn result [data-a show-data-a]
  [:div {:style {:overflow "hidden"
                 ;:box-shadow "0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px rgba(0, 0, 0, 0.23)"
                 :max-height "100%"
                 :max-width "100%"
                 :min-height "100%"
                 :min-width "100%"
                 :height "100%"
                 :width "100%"
                 :display (when-not @show-data-a "none")}}
   [segment (dissoc @data-a :code)]])

(defn floating-window [id {:keys [kernel ns render-fn data]
                           :or {kernel :clj}}]
  (let [data-a (r/atom {:data data :render-fn render-fn})
        opts {:ns ns :kernel kernel}
        show-data-a (r/atom false)]
    (fn [id _ _]
      (let [{:keys [data render-fn err out]} @data-a]
        [:<>
         [menu id opts data-a show-data-a]
         [result data-a show-data-a]
         [cm-editor id show-data-a]]))))

(defn show-floating-repl [{:keys [kernel code ns data render-fn]
                           :or {kernel :clj
                                code ""} :as opts}]
  (let [id (str (nano-id 5))]
    (overlay-add id [rnd {:bounds "window"
                          :default {:width 400 :height 400 :x 50 :y 60}
                          :style {:position  "fixed"
                                  :border "solid 2px blue"
                                  :background "#f0f0f0"
                                  :display "grid"
                                  :grid-template-rows "0px 34px 1fr"
                                  :width "100%"
                                  :height "100%"
                                  :max-height "100%"
                                  :max-width "100%"}}

                     [floating-window id opts]])
    (cm-set-code id code)))

(defn show-floating-repl-namespace
  [{:keys [kernel ns]
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
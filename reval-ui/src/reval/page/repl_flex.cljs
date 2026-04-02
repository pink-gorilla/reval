(ns reval.page.repl-flex
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [promesa.core :as p]
   [nano-id.core :refer [nano-id]]
   [shadowx.core :refer [get-resource-path]]
   [uix.core :refer [$]]
   [clj-service.http :refer [clj]]
   ; flexlayout
   [layout.flexlayout.core2 :as flc]
   ["flexlayout-react" :refer [Actions]]
   ; reval
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.dali.viewer.notebook :refer [add-segment empty-notebook]]
   [reval.notebook-ui.editor-tab :as edtab]))

;; Merged tab state for the selected repl file tab (keyboard / toolbar).
(defonce repl-selection-a (r/atom nil))

(defonce explorer-highlight-res-path
  (r/atom ""))

;; One-shot file to open when the shell mounts; not read from the URL.
(defonce pending-open-file (r/atom nil))

(defn filename-only [res-path]
  (when-not (str/blank? res-path)
    (let [s (str res-path)
          i (.lastIndexOf s "/")]
      (if (neg? i) s (subs s (inc i))))))

(defn normalize-nbinfo [m]
  (when m
    (let [m (into {} (filter (comp some? val) m))
          name-full (or (:name-full m) (some-> m :res-path str not-empty))
          ext (or (:ext m) "clj")]
      (cond-> (dissoc m :res-path)
        name-full (assoc :name-full name-full)
        ext (assoc :ext ext)))))

(defn- flex-data-a []
  (when-let [s @flc/state-a]
    (:data-a s)))

(defn sync-repl-selection! []
  (let [sid @flc/selected-id-a
        da (flex-data-a)
        entry (when da (get @da sid))
        entry (if (and (map? entry) (= :repl-file (:kind entry)))
                (:st entry)
                entry)]
    (reset! repl-selection-a entry)
    (reset! explorer-highlight-res-path (str (or (:res-path entry) "")))))

(defn config-map [x]
  (cond
    (nil? x) {}
    (map? x) x
    :else (try (js->clj x :keywordize-keys true)
               (catch :default _ {}))))

(defn eval-current-segment! []
  (when-let [{:keys [editor-id fmt nb-a]} @repl-selection-a]
    (when-let [code (edtab/current-expression editor-id)]
      (let [opts {:code code :kernel (keyword (or fmt :clj))}]
        (-> (kernel-eval opts)
            (p/then #(swap! nb-a add-segment %)))))))

(defn eval-all-in-selection! []
  (when-let [{:keys [editor-id fmt nb-a]} @repl-selection-a]
    (when-let [code (edtab/cm-get-code editor-id)]
      (reset! nb-a (empty-notebook))
      (let [opts {:code code :kernel (keyword (or fmt :clj))}]
        (-> (kernel-eval opts)
            (p/then #(swap! nb-a add-segment %)))))))

(defn eval-notebook-in-selection! []
  (when-let [{:keys [nb-a nbns]} @repl-selection-a]
    (when-not (str/blank? (str nbns))
      (let [rp (clj {:timeout 120000}
                    'reval.document.notebook/eval-notebook nbns)]
        (p/then rp (fn [r]
                     (reset! nb-a (:data r))))))))

(defn save-in-selection! []
  (when-let [{:keys [editor-id path res-path]} @repl-selection-a]
    (edtab/save-code! editor-id path res-path)))

(defn clear-output-in-selection! []
  (when-let [{:keys [nb-a]} @repl-selection-a]
    (reset! nb-a (empty-notebook))))

(defn- existing-file-tab-id-for-res-path [res-path]
  (let [rp (str res-path)
        da (flex-data-a)]
    (when (and da (not (str/blank? rp)))
      (or (some (fn [[tab-id v]]
                  (when (and (map? v)
                             (= :repl-file (:kind v))
                             (= rp (str (get-in v [:st :res-path]))))
                    tab-id))
                @da)
          (some (fn [[tab-id v]]
                  (when (and (map? v) (not (:kind v)) (= rp (str (:res-path v))))
                    tab-id))
                @da)))))

(defn- select-existing-file-tab! [tab-id]
  (when-let [^js model (:model @flc/state-a)]
    (when tab-id
      (.doAction model (Actions.selectTab tab-id)))))

(defn open-file-from-explorer! [nbinfo]
  (when-let [nbinfo (some-> nbinfo normalize-nbinfo)]
    (when-not (str/blank? (str (:name-full nbinfo)))
      (let [rp (:name-full nbinfo)]
        (if-let [existing-id (existing-file-tab-id-for-res-path rp)]
          (select-existing-file-tab! existing-id)
          (let [eid (str "repl-cm-" (nano-id 8))
                tab-name (or (filename-only rp) (:nbns nbinfo) "file")
                path-str (when (:path nbinfo) (str (:path nbinfo)))
                cfg {:nbns (:nbns nbinfo)
                     :path path-str
                     :res-path rp
                     :ext (:ext nbinfo)}
                st (merge {:nb-a (r/atom (empty-notebook))
                           :editor-id eid
                           :fmt :clj}
                          cfg)
                id-code (str "repl-code-" (nano-id 6))
                id-nb (str "repl-nb-" (nano-id 6))
                outer-id (str "repl-file-" (nano-id 6))
                cfg-shell (assoc cfg
                            :repl-inner-code-id id-code
                            :repl-inner-nb-id id-nb
                            :repl-tab-name tab-name)]
            (when-let [da (:data-a @flc/state-a)]
              (swap! da assoc id-code st id-nb st))
            (flc/add-node {:type "tab"
                           :name tab-name
                           :component "reval-repl-file-layout"
                           :config cfg-shell
                           :state {:kind :repl-file
                                   :code-id id-code
                                   :nb-id id-nb
                                   :st st}
                           :id outer-id
                           :enableClose true
                           :dock :center})))))))

(defn consume-pending-open-file! []
  (when-let [m @pending-open-file]
    (reset! pending-open-file nil)
    (when-let [nb (normalize-nbinfo m)]
      (when-not (str/blank? (str (:name-full nb)))
        (js/setTimeout #(open-file-from-explorer! nb) 400)))))

(defonce repl-model-js
  (clj->js
   {:global {:tabEnableClose true}
    :layout
    {:type "row"
     :children
     [{:type "tabset"
       :children
       [{:type "tab"
         :name "Start"
         :component "reval-repl-welcome"
         :enableClose false}]}]}
    ;; IJsonBorderNode: :selected -1 => no border tab selected => Files panel collapsed (undocked).
    ;; https://caplin.github.io/FlexLayout/demos/v0.8/typedoc/interfaces/IJsonBorderNode.html
    :borders
    [{:type "border"
      :location "left"
      :size 300
      :selected -1
      :children
      [{:type "tab"
        :id "reval-explorer-border"
        :name "Files"
        :component "reval-repl-explorer"
        :enableClose false}]}]}))

(def ^:private repl-header-height-px 40)

(defn repl-header []
  [:<>
   [:link {:href (str (get-resource-path) "flexlayout-react/style/light.css")
           :rel "stylesheet"}]
   [:span {:style {:font-weight 600}} "REPL"]])

(def ^:private repl-flexlayout-opts
  {:layout-json repl-model-js
   :category "reval"
   :model-name "repl"
   :data {}})

(defn- repl-flex-inner []
  [:div
   {:style
    {:position "fixed"
     :top 0
     :left 0
     :right 0
     :bottom 0
     :margin 0
     :padding 0
     :box-sizing "border-box"
     :display "flex"
     :flex-direction "column"
     :overflow "hidden"}}
   [:div
    {:style
     {:height (str repl-header-height-px "px")
      :flex "0 0 auto"
      :display "flex"
      :align-items "center"
      :padding "0 10px"
      :box-sizing "border-box"
      :border-bottom "1px solid #ddd"}}
    [repl-header]]
   [:div
    {:style
     {:flex "1 1 0"
      :min-height 0
      :min-width 0
      :position "relative"
      :overflow "hidden"}}
    [:div
     {:style
      {:height "100%"
       :width "100%"
       :min-height 0
       :display "flex"
       :flex-direction "column"}}
     ($ flc/flex-layout repl-flexlayout-opts)]]])

(defn repl-shell []
  (r/create-class
   {:component-did-mount
    (fn [_]
      (consume-pending-open-file!)
      (add-watch flc/selected-id-a ::reval-repl-sync
                 (fn [_ _ _ _] (sync-repl-selection!))))
    :reagent-render
    (fn [] [repl-flex-inner])}))

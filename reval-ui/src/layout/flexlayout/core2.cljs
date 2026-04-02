(ns layout.flexlayout.core2
  "Vendored from org.pinkgorilla/flexlayout with :dock on add-node (:center default, :right/:left/:top/:bottom via Model.doAction).
  Supports optional :layout-state / :selection-atom for nested Layout (sublayout) without clobbering the root state-a."
  (:require
   [taoensso.timbre :refer-macros [debug info error]]
   [reagent.core :as r]
   [reagent.ratom :as ratom]
   [nano-id.core :refer [nano-id]]
   [uix.core :refer [$ defui]]
   [uix.dom]
   ["react" :as react]
   ["flexlayout-react" :refer [Layout Model Actions TabSetNode DockLocation RowNode]]
   [layout.flexlayout.store :as store]
   [layout.flexlayout.comp :refer [component-ui]]))

(defonce state-a (r/atom {:data-a (r/atom {})}))

(defn subscribe-state-in [data-a-atom id]
  (ratom/make-reaction
   (fn [] (get @data-a-atom id))))

(defn subscribe-state [id]
  (subscribe-state-in (:data-a @state-a) id))

(defn make-component-factory [layout-state-atom _selection-atom]
  (fn [^TabSetNode node]
    (let [component (.getComponent node)
          id (.getId node)
          config (.getConfig node)
          data-a (:data-a @layout-state-atom)
          opts {:component component
                :id id
                :config config
                :state (subscribe-state-in data-a id)
                :layout-state-atom layout-state-atom}]
      (component-ui opts))))

(defonce selected-id-a (r/atom nil))

(defn subscribe-selected-state []
  (ratom/make-reaction
   (fn [] (get @(:data-a @state-a) @selected-id-a))))

(defn- data-a-from-layout-state [layout-state-atom]
  (:data-a @layout-state-atom))

(defn make-handle-action [layout-state-atom selection-atom]
  (fn [^js action]
    (when (= Actions.SELECT_TAB (.-type action))
      (let [cell-id (-> action .-data .-tabNode)]
        (println "selected tab: " cell-id)
        (reset! selection-atom cell-id)
        js/undefined))
    (when (= Actions.DELETE_TAB (.-type action))
      (let [data-a (data-a-from-layout-state layout-state-atom)
            cell-id (-> action .-data .-node)]
        (println "cell deleted: " cell-id)
        (when-let [v (get @data-a cell-id)]
          (when (= :repl-file (:kind v))
            (doseq [i (filter some? [(:code-id v) (:nb-id v)])]
              (swap! data-a dissoc i))))
        (swap! data-a dissoc cell-id)
        js/undefined))
    action))

(defui flex-layout [{:keys [layout-json category model-name data layout-state selection-atom]
                     :or {layout-state state-a
                          selection-atom selected-id-a}}]
  (let [model (Model.fromJson layout-json)
        factory (react/useMemo
                 (fn [] (make-component-factory layout-state selection-atom))
                 #js [layout-state selection-atom])
        on-action (react/useMemo
                   (fn [] (make-handle-action layout-state selection-atom))
                   #js [layout-state selection-atom])]
    ($ :div
       ($ :link {:href "/r/flexlayout-react/style/light.css"
                 :rel "stylesheet"})
       ($ Layout
          {:model model
           :factory factory
           :onAction on-action
           :ref (fn [el]
                  (swap! layout-state merge
                         {:layout el
                          :model model
                          :category category
                          :model-name model-name
                          :data-a (or (:data-a @layout-state)
                                      (r/atom (or data {})))}))}))))

(defn add-node [{:keys [id state dock]
                 :or {id (nano-id 5) dock :center}
                 :as node}]
  (let [^js model (:model @state-a)
        layout (:layout @state-a)
        data-a (:data-a @state-a)
        dock-kw (if (keyword? dock) dock (keyword (str (or dock :center))))
        tab-json (-> node
                     (assoc :id id)
                     (dissoc :state :dock))
        loc (case dock-kw
              :center (.-CENTER DockLocation)
              :right (.-RIGHT DockLocation)
              :left (.-LEFT DockLocation)
              :top (.-TOP DockLocation)
              :bottom (.-BOTTOM DockLocation)
              (.-CENTER DockLocation))
        tabset (or (.getActiveTabset model)
                   (.getFirstTabSet model))
        tsid (.getId ^TabSetNode tabset)
        node-js (clj->js tab-json)]
    (println "adding new node to layout..")
    (when state
      (swap! data-a assoc id state))
    (if (and layout (= loc (.-CENTER DockLocation)))
      (.addTabToTabSet ^js layout tsid node-js)
      (when model
        (.doAction model (Actions.addNode node-js tsid loc -1 true))))))

(defn- dock-location-kw->enum [dock-kw]
  (case dock-kw
    :center (.-CENTER DockLocation)
    :right (.-RIGHT DockLocation)
    :left (.-LEFT DockLocation)
    :top (.-TOP DockLocation)
    :bottom (.-BOTTOM DockLocation)
    (.-RIGHT DockLocation)))

(defn add-node-tree
  "Dock a full row subtree in one step (same JSON shape as nested :layout from Model.toJson —
   e.g. {:type \"row\" :children [{:type \"tabset\" :children [{:type \"tab\" ...}]} ...]}).
   Unlike Actions.addNode, this supports tabsets/splits inside the JSON via RowNode.fromJson + TabSetNode.drop.
   tab-states: map of tab node id -> map stored in data-a (same as add-node :state)."
  [row-json {:keys [tab-states dock]
             :or {dock :right}}]
  (let [^js model (:model @state-a)
        data-a (:data-a @state-a)
        dock-kw (if (keyword? dock) dock (keyword (str (or dock :right))))
        loc (dock-location-kw->enum dock-kw)]
    (when model
      (doseq [[tid st] tab-states]
        (when tid
          (swap! data-a assoc tid st)))
      (let [^js win-map (.getwindowsMap model)
            ^js layout-win (.get win-map (.-MAIN_WINDOW_ID Model))
            ^js row-root (RowNode.fromJson (clj->js row-json) model layout-win)
            ^js tabset (or (.getActiveTabset model)
                           (.getFirstTabSet model))]
        (when tabset
          (.drop ^js tabset row-root loc -1 true)
          (.updateIdMap ^js model))))))

(defn save-layout []
  (println "save-layout..")
  (if @state-a
    (let [_ (println "layout found!")
          ^js model (:model @state-a)
          category (:category @state-a)
          model-name (:model-name @state-a)
          model-clj (js->clj (.toJson model))]
      (println "model: " model-clj)
      (store/save-layout category model-name {:data @(:data-a @state-a)
                                              :model model-clj}))
    (println "no layout found. - not saving")))

(def layout-data-model-a (r/atom nil))

(defn flexlayout-model-load [opts]
  (let [model (get-in opts [:path :model])
        category (:category opts)]
    (info "flexlayout model load: category: " category " model: " model)
    (store/load-layout->atom layout-data-model-a category model)))

(defn flexlayout-with-header [header flexlayout-opts]
  [:div {:style {:height "100vh"
                 :width "100vw"
                 :top "0"
                 :left "0"
                 :margin "0"
                 :padding "0"
                 :display "flex"
                 :flex-direction "column"
                 :flex-grow 1}}
   [:div {:dir "ltr"
          :style {:margin "2px"
                  :display "flex"
                  :align-items "center"}}
    [header]]
   [:div {:style {:display "flex"
                  :flex-grow "1"
                  :position "relative"
                  :border "1px solid #ddd"}}
    [:div ($ flex-layout flexlayout-opts)]]])

(defn flexlayout-only [flexlayout-opts]
  ($ flex-layout flexlayout-opts))

(defn flexlayout-page [{:keys [parameters] :as match}]
  (if-let [{:keys [model data]} @layout-data-model-a]
    (let [category (get-in match [:data :category])
          header (get-in match [:data :header])
          model-js (clj->js model)
          model-name (get-in parameters [:path :model])
          flexlayout-opts {:layout-json model-js
                           :category category
                           :model-name model-name
                           :data data}]
      (println "model started: " model-name " category: " category)
      (if header
        [flexlayout-with-header header flexlayout-opts]
        [flexlayout-only flexlayout-opts]))
    [:div "loaded model is nil."]))

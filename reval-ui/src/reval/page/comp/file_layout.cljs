(ns reval.page.comp.file-layout
  "Flexlayout component registrations for the repl (side-effect ns)."
  (:require
   [nano-id.core :refer [nano-id]]
   [uix.core :refer [$ defui]]
   ["react" :as react]
   [layout.flexlayout.core2 :as flc]
   [layout.flexlayout.comp :refer [component-ui]]
   [reval.page.repl-flex :as rflex]))

(defn- repl-file-inner-config [cfg]
  (dissoc cfg :repl-inner-code-id :repl-inner-nb-id :repl-tab-name))

(defui repl-file-layout-pane [opts]
  (let [cfg (rflex/config-map (:config opts))
        id-code (:repl-inner-code-id cfg)
        id-nb (:repl-inner-nb-id cfg)
        tab-n (str (:repl-tab-name cfg))
        tcfg (repl-file-inner-config cfg)
        layout-json (react/useMemo
                     (fn []
                       (let [rid (str "sr-" (nano-id 8))
                             ts1 (str "st1-" (nano-id 6))
                             ts2 (str "st2-" (nano-id 6))]
                         (clj->js
                          {:global {:tabEnableClose false
                                    :tabSetEnableClose false}
                           :layout {:type "row"
                                    :id rid
                                    :children [{:type "tabset"
                                                :id ts1
                                                :weight 50
                                                :selected 0
                                                :children [{:type "tab"
                                                            :id id-code
                                                            :name tab-n
                                                            :component "reval-repl-code"
                                                            :config tcfg
                                                            :enableClose false}]}
                                               {:type "tabset"
                                                :id ts2
                                                :weight 50
                                                :selected 0
                                                :children [{:type "tab"
                                                            :id id-nb
                                                            :name (str tab-n " · out")
                                                            :component "reval-repl-notebook"
                                                            :config tcfg
                                                            :enableClose false}]}]}})))

                     #js [id-code id-nb tab-n (str (:res-path cfg)) (str (:nbns cfg))])
        layout-state (react/useMemo
                      (fn [] (atom {:data-a (:data-a @flc/state-a)}))
                      #js [])]
    ($ :div {:style {:height "100%" :width "100%" :min-height 0 :position "relative"}}
       ($ flc/flex-layout {:layout-json layout-json
                           :layout-state layout-state
                           :selection-atom flc/selected-id-a
                           :category "reval"
                           :model-name "repl-file-sub"
                           :data {}}))))

(defmethod component-ui "reval-repl-file-layout" [opts]
  ($ repl-file-layout-pane opts))


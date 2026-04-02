(ns demo.page.directory-explorer
  (:require
   [reval.dali.viewer.directory-explorer-viewer :refer [directory-explorer-viewer]]
   [reval.page.repl :as repl]))

(defn- goto-repl [nbinfo]
  (repl/open-in-repl! nbinfo))

(defn page [_match]
  [:div {:style {:height "100vh"
                 :display "flex"
                 :flex-direction "column"
                 :background "#f3f4f6"}}
   [:div {:style {:padding "12px 16px" :background "#e5e7eb" :border-bottom "1px solid #d1d5db"}}
    [:h1 {:style {:margin 0 :font-size "1.125rem" :font-weight 600}}
     "Directory explorer"]
    [:p {:style {:margin "6px 0 0 0" :font-size "0.875rem" :color "#4b5563"}}
     "Notebook sources under configured resource roots. Click a file to open it in the repl."]]
   [:div {:style {:flex 1 :min-height 0 :padding "8px"}}
    [directory-explorer-viewer
     {:link (fn [node]
              (goto-repl
               (cond-> {:nbns (:nbns node)
                        :ext (:ext node)}
                 (:path node) (assoc :path (str (:path node)))
                 (:name-full node) (assoc :name-full (:name-full node)))))}]]])

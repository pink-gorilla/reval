(ns reval.page.repl
  (:require
   reval.page.repl-comp ;; side effects: register flexlayout tab components
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]
   [reval.page.repl-flex :as flex]))

(defn open-in-repl! [nbinfo]
  (reset! flex/pending-open-file nbinfo)
  (rfe/navigate 'reval.page.repl/repl-page))

(rf/reg-event-fx
 :repl/eval-expression
 (fn [_ _]
   (flex/eval-current-segment!)
   nil))

(defn repl-page [_route]
  [flex/repl-shell])

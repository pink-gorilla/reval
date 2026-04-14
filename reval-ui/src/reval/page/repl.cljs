(ns reval.page.repl
  (:require
   [re-frame.core :as rf]
   [reitit.frontend.easy :as rfe]
   [reval.page.repl-flex :as flex]
    ;; side effects 
   [reval.page.comp.welcome]
   [reval.page.comp.explorer]
   [reval.page.comp.notebook]
   [reval.page.comp.code]
   [reval.page.comp.file-layout]))

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

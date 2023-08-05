(ns reval.goldly.notebook-ui.eval
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [goldly :refer [error-view]]
   [goldly.service.core :as service]
   [goldly.sci :refer [compile-sci-async]]
   [reval.goldly.viz.data :refer [value->data]]
   ))

(defn eval-cljs [on-evalresult {:keys [code _ns]}]
  (let [er-p (compile-sci-async code)]
    (-> er-p
        (.then
         (fn [er]
           (when [er] ; {:id :code :value :err :out :ns}
             (let [er-h (-> er
                            (dissoc :value)
                            (merge (value->data (:value er)))
                            (assoc :out (js->clj (:out er)))
                            )]
               ;(.log js/console "eval result cljs: " (pr-str er-h))
               (on-evalresult er-h)
               ;(reset! cljs-er er-h)
               ))))
        (.catch (fn [e]
                  (.log js/console "eval failed: " e)
                  (when-let [sci-err (goldly/exception->error e)]
                    ;(reset! cljs-er sci-err)
                    (on-evalresult {:code code :err-sci sci-err})
                    ))))))

(defonce cur-ns (r/atom "user"))

(defn eval-clj [on-evalresult opts]
  (let [opts (merge opts {:ns @cur-ns})]
  (service/run-cb
   {:fun 'reval.viz.eval/viz-eval
    :args [opts]
    :timeout 60000
    :cb (fn [[s {:keys [result]}]]
             (let [{:keys [ns]} result]
                                        ;(println "clj-eval result: " result)
               (on-evalresult result)
               ;(reset! clj-er {:er result})
               ;(println "setting ns to: " ns)
               (reset! cur-ns ns)))})))

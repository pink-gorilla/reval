(ns reval.goldly.viz.render
  (:require
   [reagent.core :as r]
   [goldly.sci :refer [require-async resolve-symbol-sci]]))

(defn log [& args]
  (.log js/console (apply str args)))

(defn loading-ui [s]
  [:p "loading: " (pr-str s)])

(defn load-error-ui [s]
  [:p "render load error: " (pr-str s)])

(defn create-loader-ui [load-atom s]
  (fn [& args]
    (fn [& args]
      (let [{:keys [status fun]} @load-atom]
        (case status
          :loading [loading-ui s]
          :error [load-error-ui s]
          :loaded (into [fun] args)
          [loading-ui s])))))

; similar to pinkie
(defn get-render-fn [s]
  (if-let [fun (resolve s)]
    fun
    (let [load-atom (r/atom {:status :loading})
          libspec [(-> s namespace symbol)]
          require-p (require-async libspec)]
      (log "get-render-fn requiring libspec: " libspec)
      (.then require-p (fn [d]
                         (log "get-render-fn: require result received!")
                         (let [f (resolve s)]
                           (log "get-render-fn resolved fun: " s)
                           (if f
                             (swap! load-atom assoc :status :loaded :data d :fun f)
                             (swap! load-atom assoc :status :error)))))
      (.catch require-p (fn [d]
                          (log "get-render-fn: require result failure!")
                          (swap! load-atom assoc :status :error)))
      (create-loader-ui load-atom s))))






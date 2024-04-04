(ns reval.goldly.viz.render
  (:require
   [reagent.core :as r]
   [webly.spa.resolve :refer [get-resolver]]))

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
          resolve-fn (get-resolver)
          resolve-p (resolve-fn s)]

      (.then resolve-p (fn [f]
                         (log "get-render-fn resolved fun: " s)
                         (if f
                           (swap! load-atom assoc :status :loaded :data :d :fun f)
                           (swap! load-atom assoc :status :error))))
      (.catch resolve-p (fn [d]
                          (log "get-render-fn: require result failure!")
                          (swap! load-atom assoc :status :error)))
      (create-loader-ui load-atom s))))






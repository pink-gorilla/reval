(ns reval.goldly.viz.data
  (:require
   [reval.type.converter :refer [value-type->hiccup]]))

(def nil-view
  [:div.p-2.clj-nil
   [:p "nil"]])

(defn value->data
  "converts a eval result value to a ui-data structure"
  [v]
  (if v
    (let [m (meta v)]
      (cond

        (contains? m :render-fn)
        {:render-fn (:render-fn m)
         :data v}

        (contains? m :render-fn-escaped)
        {:render-fn (:render-fn-escaped m)
         :data (:data v)}

        (contains? m :hiccup)
        {:render-fn 'reval.goldly.viz.render-fn/hiccup
         :data v}

        (contains? m :reagent)
        {:render-fn 'reval.goldly.viz.render-fn/reagent
         :data v}

        (contains? m :R)
        {:render-fn 'reval.goldly.viz.render-fn/reagent
         :data v}

        :else
        {:render-fn 'reval.goldly.viz.render-fn/reagent
         :data (value-type->hiccup v)}))
    {:render-fn 'reval.goldly.viz.render-fn/reagent
     :data nil-view}))

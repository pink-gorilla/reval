


; ; (defmethod reagent-page :demo/main [& args]


#_(defn available-pages []
    (->> (methods reagent-page)
         keys
         (remove #(= :default %))
         (into [])))
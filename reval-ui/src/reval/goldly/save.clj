(ns reval.goldly.save)

(defn save-code [{:keys [path code]}]
  (println "saving code to: " path)
  (spit path code))
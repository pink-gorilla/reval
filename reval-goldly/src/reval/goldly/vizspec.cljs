(ns reval.goldly.vizspec
  (:require
   [user]
   [viz]))

(defn safe-resolve-renderer [s]
  (try
    (user/resolve-symbol-sci s)
    (catch :default e
      ;(println "renderer not found: " s)
      nil)))

(defn render-vizspec2 [h]
  ;(println "rendering vizspec: " h)
  ;(println "first item in vec:" (first h) "type: " (type (first h)))
  ;(println "render fn:" (get-symbol-value (first h)))
  ;(println "now showing..")
  (let [h-fn (viz/show safe-resolve-renderer h)]
    ;(println "rendered spec: " (pr-str h-fn))
    h-fn))

(defn show [h]
  (with-meta
    h
    {:R true}))

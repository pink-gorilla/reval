(ns reval.config
  (:require
   [dali.store.file :refer [create-dali-file-store]]
   [reval.default]  ; side effects to include all default converters
   ))

(def default-config
  {:rdocument {:fpath ".reval/public/rdocument"
               :rpath "/r/rdocument"
               :url-root "/api/rdocument/file/"}
   :namespace-root ["notebook" "user" "demo"]
   :clones-root ".reval/clones"})

(defn start-reval [config]
  (assoc config :dali-store (create-dali-file-store (:rdocument config))))

(def reval
  (start-reval default-config))

(defn configure-reval [config]
  (let [r (start-reval config)]
    (alter-var-root #'reval (constantly r))
    nil))

#_(def collections {:user [:clj "user/notebook/"]
                    :demo [:clj "demo/notebook/"]
                  ;:demo-cljs [:cljs "demo/notebook/"])
                    })
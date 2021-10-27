(ns demo.notebook
  (:require
   [clojure.edn :as edn]
   [clojure.core.async :refer [<! <!! >! chan close! go timeout]]
   [taoensso.timbre :as timbre :refer [debugf info error]]
   [picasso.default-config] ; side-effects
   [notebook.transactor :refer [exec notebook]]
   [picasso.kernel.edn]
   [picasso.kernel.clj])
  (:gen-class))

(defn -main [mode]
  (case mode
    "new" (do (info "creating nb and saving to demo-nb.edn")
              (exec [:new-notebook])
              (exec [:set-meta-key :title "auto generated notebook"])
              (exec [:set-meta-key :tags #{:cool :auto-generated}])
              (exec [:add-code :clj "(+ 7 7)"])
              (exec [:add-md "# hello, world"])
              (exec [:add-code :edn "{:a 1 :b 2}"])
              (exec [:eval-all]))

    "add" (let [doc (-> (slurp "demo-nb.edn") (edn/read-string))]
            (exec [:load-notebook doc])
            (exec [:add-code :clj "(def b [x] (+ b 1000))"])
            (exec [:eval-all])))

  (<!! (go (<! (timeout 3000))))
  (info "saving..")
  (spit "demo-nb.edn" (pr-str (notebook))))
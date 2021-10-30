(ns reval.kernel.clj-test
  (:require
   [clojure.test :refer [deftest are]]
   [clojure.core.async :refer [<! go]]
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.default]  ; side-effects
   [reval.kernel.clj] ; add clj kernel
   [reval.type.clj] ; bring the renderers into scope
   [reval.ns-eval :refer [eval-result->hiccup]]))

(defn r= [a b]
  (= (dissoc a :id) b))

(deftest test-eval-clj
  "eval with several expressions"
  (go (are [input-clj result]
           (r=  (-> (<! (kernel-eval {:kernel :clj :code input-clj}))
                    eval-result->hiccup
                    (dissoc :id))
                result)

        ; eval-result->hiccup does NOT show VALUES. They are gone.
        "13"
        {:src "13"
         :out ""
         :hiccup [:span {:class "clj-long"} "13"]}

        #_"[7 8]"
        #_{:picasso
           {:type :list-like
            :content
            {:class "clj-vector"
             :open "["
             :close "]"
             :separator " "
             :items
             '({:type :hiccup, :content [:span {:class "clj-long"} "7"]}
               {:type :hiccup, :content [:span {:class "clj-long"} "8"]})
             :value "[7 8]"}}})))

(ns reval.kernel.clj-test
  (:require
   [clojure.test :refer [deftest are]]
   [clojure.core.async :refer [<! go]]
   [reval.kernel.protocol :refer [kernel-eval]]
   [reval.type.default]  ; side-effects
   [reval.kernel.clj] ; add clj kernel
   ))

(defn r= [a b]
  (= (dissoc a :id) b))

(deftest test-eval-clj
  "eval with several expressions"
  (go (are [input-clj result]
           (r= (<! (kernel-eval {:kernel :clj :code input-clj}))
               result)

        "13"
        {:src "13"
         :value 13 
         :out ""
         :picasso {:type :hiccup, :content [:span {:class "clj-long"} "13"]}
         } 

        "[7 8]"
        {:picasso
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

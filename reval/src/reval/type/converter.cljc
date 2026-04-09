(ns reval.type.converter
  (:require
   [dali.spec :refer [create-dali-spec]]
   [reval.type.protocol :refer [dali-convertable to-dali]]
   ))


;; UNKNOWN

(defn unknown-type [v]
  (let [type-as-str (-> v type str)]
    (create-dali-spec
     {:viewer-fn 'dali.viewer.hiccup/hiccup
      :data  [:div.border-solid.p-2.dali-unknown-type
              [:p.text-red-300 "unknown type: " type-as-str]
              [:span (pr-str v)]]})))

;; ERROR

(defn type-convert-err [v]
  (create-dali-spec
   {:viewer-fn 'dali.viewer.hiccup/hiccup
    :data  [:div.border-solid.p-2.dali-type-convert-err
            [:p.text-red-300 "type convert error"]
            ]}))


#?(:clj
   (defn type->dali [v] ; here env is first
     (try
       (if (satisfies? dali-convertable v)
         (to-dali v)
         (unknown-type v))
       (catch Exception ex
         (type-convert-err v))
       (catch Throwable t
         (println "T")
         )
       
       ))

   :cljs
   (defn type->dali [v] 
     (try
       (to-dali v)
       (catch js/Exception _
         (unknown-type v)))))



(comment 
  (require '[tablecloth.api :as tc])
  (def ds (tc/dataset {:a [1 2 3] :b [4 5 6]}))
  
  (type->dali (:a ds))
  (type->dali ds)
  

  (keys ds)
  (vals ds)

  ;; ds satisfies dali-convertable. dont know why.
  (satisfies? dali-convertable ds)
  (satisfies? dali-convertable (:a ds))
  
  (satisfies? dali-convertable (type ds))
  (satisfies? dali-convertable (type (:a ds)))

  
  (satisfies? dali-convertable (type 54))

  (extends? dali-convertable (type ds))
  (extends? dali-convertable (type (:a ds)))


  (extends? dali-convertable (type 44 ))
  

  (type ds)
  
  (.getInterfaces (type ds))
 ; 
  )
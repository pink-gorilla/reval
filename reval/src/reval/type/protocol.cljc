(ns reval.type.protocol
  (:require
   [dali.spec])
  #?(:clj (:import [dali.spec DaliSpec])))

(defprotocol dali-convertable
  (to-dali [v]))

#?(:clj
   (extend-type DaliSpec
  ; dali-spec does not need to be converted.
     dali-convertable
     (to-dali [v]
       v))

   :cljs
   (extend-type dali.spec/DaliSpec
  ; dali-spec does not need to be converted.
     dali-convertable
     (to-dali [v]
       v)))



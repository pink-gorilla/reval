(ns reval.type.protocol)

(defprotocol hiccup-convertable
  (to-hiccup [self]))

(defprotocol hiccup-convertable-reproduceable
  (to-hiccup-reproduceable [ns self]))





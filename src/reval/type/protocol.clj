(ns reval.type.protocol)

(defprotocol hiccup-convertable
  (to-hiccup [self]))

(defn span-render
  [thing class]
  [:span {:class class} (pr-str thing)])




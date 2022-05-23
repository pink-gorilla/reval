(ns reval.type.ui.simplevalue)

(def styles
  {"clj-raw"    {:color "red"}
   "clj-nil"    {:color "grey"}
   "clj-symbol" {:color  "steelblue"}
   "clj-namespace" {:color "steelblue"}
   "clj-keyword" {:color "rgb(30, 30, 82)"}
   "clj-var" {:color "deeppink"}
   "clj-atom" {:color "darkorange"}
   "clj-agent" {:color "darkorange"}
   "clj-ref" {:color "darkorange"}

   "clj-char" {:color "dimgrey"}
   "clj-string" {:color "grey"}

   "clj-int" {:color "blue"}
   "clj-long" {:color "blue"}
   "clj-bigint" {:color "blue"}

   "clj-float" {:color "darkgreen"}
   "clj-double" {:color "darkgreen"}
   "clj-bigdecimal" {:color "darkgreen"}
   "clj-ratio" {:color "darkgreen"}

   "clj-localdate" {:color "green"}})

(defn class->style [c]
  (if-let [s (get styles c)]
    {:style s}
    {:class c}))

(defn simplevalue->hiccup
  [thing class]
  [:span (class->style class)
   (pr-str thing)])


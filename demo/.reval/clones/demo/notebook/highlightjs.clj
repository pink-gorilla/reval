(ns demo.notebook.highlightjs)

;; this comes from highlightjs
;; but it is a clone.

^:R
[:div.bg-blue-500 ; test how background is on different color
 [:p "this snippet demonstrates to use highlight.js"]
 ['ui.highlightjs/highlightjs "(+ 8 8)\n(def add [a b] \n   (+ a b))\n(add)"]
 [:p "live goes on!"]]


^:R
[:div.bg-blue-500 ; test how background is on different color
 [:p "look at --> "]
 ['ui.highlightjs/highlightjs "(->> (map inc (range 10))\n   (take 3))"]
 [:p "live goes on!"]]
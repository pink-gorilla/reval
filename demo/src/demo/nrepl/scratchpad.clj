(ns demo.nrepl.scratchpad
  (:require
   [modular.persist.protocol :refer [loadr]]
   [reval.type.converter :refer [value->hiccup]]
   [reval.kernel.clj-eval :refer [clj-eval-raw]] ; side effects
   [reval.ui :as ui]))

(tap> 5)

(defn h> [x]
  (-> x
      (with-meta {:R true})
      tap>))

(h> [:p "hello, world!"])

(h> [:p "hello, demo!"])

; evaled expression inside the hiccup.
(h> [:p "Multiplication result: " (* 7 7)])

; code in highlightjs
(h> ['ui.codemirror/codemirror-atom "(println 5) ; this is code"])

; show pinkie tags!
(h> ['ui.clock/clock])

; show image (in static resource) 
(h> [:img {:src "/r/sun.png"}])

; show image (dynamically generated)
; since we lack a way to generate images here, all we
; do is load an image from the public directory.
(-> (loadr :png  "demo/public/sun.png")
    ui/img-inline
    ;to-hiccup
    h>)
;; the resulting image is rendered as inline base64 image.

(-> (loadr :png  "demo/public/sun.png")
    ui/img
    ;value->hiccup
    h>)
; [:img {:src "/api/rdocument/file/demo.scratchpad/7f9fdfa1-fcde-411f-8022-717337664a41.png"
;        :width 192, :height 187, :alt ""}]

; test eval error ui
(h> ['user/evalerr (:err (clj-eval-raw "(+ 3 4"))])

(h> [:p.text-red-500 "hello world"])
(h> ^:hiccup [:p.text-red-500 "hello world"])








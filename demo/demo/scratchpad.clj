(ns demo.scratchpad
  (:require
   [goldly.scratchpad :refer [show! clear!]]
   [reval.ui :as ui]
   [reval.persist.protocol :refer [loadr]]
   [reval.type.converter :refer [value->hiccup]]
   [demo.init] ; side effects
   ))


(show! [:p "hello, demo!"])



; evaled expression inside the hiccup.
(show! [:p "Multiplication result: " (* 7 7)])

; code in highlightjs
(show! [:p/code "(println 5) ; this is code"])

; show pinkie tags!
(show! [:p/clock])

; show image (in static resource) 
(show! [:img {:src "/r/sun.png"}])


; show image (dynamically generated)
; since we lack a way to generate images here, all we
; do is load an image from the public directory.
(-> (loadr :png  "demo/public/sun.png")
    ui/img-inline
    ;to-hiccup
    show!)
;; the resulting image is rendered as inline base64 image.


(-> (loadr :png  "demo/public/sun.png")
    ui/img
    ;value->hiccup
    show!
    )
; [:img {:src "/api/rdocument/file/demo.scratchpad/7f9fdfa1-fcde-411f-8022-717337664a41.png"
;        :width 192, :height 187, :alt ""}]


;

(defmacro cljs [render-fn & args]
  (into
   [(str render-fn)]
   args))



(macroexpand
 (cljs2 :code "(println 5)"))







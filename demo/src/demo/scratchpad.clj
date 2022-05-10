(ns demo.scratchpad
  (:require
   [modular.persist.protocol :refer [loadr]]
   [reval.type.converter :refer [value->hiccup]]
   [reval.kernel.clj-eval :refer [clj-eval-raw]] ; side effects
   [reval.ui :as ui]
   [scratchpad.core ;:refer [show! clear!]
    ]
  ; [demo.init] ; side effects
   ))
(show! [:p "hello, demo!"])

; evaled expression inside the hiccup.
(show! [:p "Multiplication result: " (* 7 7)])

; code in highlightjs
(show! ['user/codemirror "(println 5) ; this is code"])

; show pinkie tags!
(show! ['user/clock])

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
    show!)
; [:img {:src "/api/rdocument/file/demo.scratchpad/7f9fdfa1-fcde-411f-8022-717337664a41.png"
;        :width 192, :height 187, :alt ""}]

; test eval error ui
(show! ['user/evalerr (:err (clj-eval-raw "(+ 3 4"))])

(show! [:p.text-red-500 "hello world"])
(show! ^:hiccup [:p.text-red-500 "hello world"])

(value->hiccup
 ^:fh
 ['user/evalerr (:err (clj-eval-raw "(+ 3 4"))])

(defn eval-err [e]
  (value->hiccup
   ^:fh
   ['user/evalerr (:err (clj-eval-raw "(+ 3 4"))]))

(show! (eval-err (:err (clj-eval-raw "(+ 3 4"))))

;

(defmacro cljs [render-fn & args]
  (into
   [(str render-fn)]
   args))

(macroexpand
 (cljs2 :code "(println 5)"))







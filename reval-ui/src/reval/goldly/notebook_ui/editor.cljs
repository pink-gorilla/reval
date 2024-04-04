(ns reval.goldly.notebook-ui.editor
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [cm]
   [ui.codemirror :refer [codemirror-unbound]]
   [goldly :refer [error-view]]
   [goldly.service.core :as service]
   [reval.editor :refer [block-for]]))

(defonce editor-id (r/atom 1))

(defn cm-get-code []
  (-> (cm/get @editor-id)
      (cm/get-code)))

(defn cm-set-code [code]
  (let [c (cm/get @editor-id)]
    (cm/set-code c code)
    (cm/focus c)))

(defn save-code [path]
  (let [code (cm-get-code)]
    (service/run-cb {:fun 'reval.goldly.save/save-code
                     :args [{:code code :path path}]
                     :timeout 1000
                     :cb (fn [[_s {:keys [_result]}]]
                   ;(println "result: " result)
                           )})))

(def cm-opts {:lineWrapping false})

(defn style-codemirror-fullscreen []
  ; height: auto; "400px" "100%"  height: auto;
  ; auto will make the editor resize to fit its content (i
  [:style ".my-codemirror > .CodeMirror { 
              font-family: monospace;
              height: 100%;
              min-height: 100%;
              max-height: 100%;
            }"])

;(defn cm-editor-atom []
;  [:div.w-full.h-full.bg-white-200
;    [style-codemirror-fullscreen]
;      [ui.codemirror/codemirror @editor-id repl-code]])

(defn cm-editor []
  [:<> [style-codemirror-fullscreen] ;cm/style-inline
   [:div.my-codemirror.w-full.h-full
    [codemirror-unbound @editor-id cm-opts]]])

(defn current-expression []
  (let [id @editor-id]
    (when-let [c (cm/get id)]
      (let [p (cm/cursor-position c)
            {:keys [line col]} p
            code (cm-get-code)
            ;code "(+ 3 1)\n(* 3 4 5 \n   6 7)\n(println 55)"
            cur-exp (block-for code [line col])
            code-exp (second cur-exp)]
        ;cur-exp
        code-exp))))

(defn print-position []
  (when-let [_code-exp (current-expression)]
    ;(info code-exp)
    ))

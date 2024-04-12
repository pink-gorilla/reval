(ns reval.notebook-ui.editor
  (:require
   [reagent.core :as r]
   [ui.codemirror.theme :as theme]
   [ui.codemirror.api :as api]
   [ui.codemirror.codemirror :refer [codemirror get-editor]]
   [goldly.service.core :as service]
   [reval.notebook-ui.rewrite :refer [block-for]]))

(defonce editor-id (r/atom 27))

(defn cm-get-code []
  (-> (get-editor @editor-id)
      (api/get-code)))

(defn cm-set-code [code]
  (let [c (get-editor @editor-id)]
    (api/set-code c code)
    (api/focus c)))

(defn save-code [path]
  (let [code (cm-get-code)]
    (service/run-cb {:fun 'reval.save/save-code
                     :args [{:code code :path path}]
                     :timeout 1000
                     :cb (fn [[_s {:keys [_result]}]]
                   ;(println "result: " result)
                           )})))

(def cm-opts {:lineWrapping false})

(defn cm-editor []
  [:<>
   [theme/style-codemirror-fullscreen] ;cm/style-inline
   [:div.my-codemirror.w-full.h-full
    [codemirror @editor-id cm-opts]]])

(defn current-expression []
  (let [id @editor-id]
    (when-let [c (get-editor id)]
      (let [p (api/cursor-position c)
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

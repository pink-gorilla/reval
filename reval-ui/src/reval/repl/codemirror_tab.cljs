(ns reval.repl.codemirror-tab
  "CodeMirror helpers keyed by editor id (multi-tab repl)."
  (:require
   [clojure.string :as str]
   [clj-service.http :refer [clj]]
   [reval.repl.rewrite :refer [block-for]]
   [ui.codemirror.api :as api]
   [ui.codemirror.codemirror :refer [get-editor]]))

(defn cm-get-code [editor-id]
  (when-let [c (get-editor editor-id)]
    (api/get-code c)))

(defn cm-set-code [editor-id code]
  (when-let [c (get-editor editor-id)]
    (api/set-code c code)
    (api/focus c)))

(defn save-code! [editor-id path res-path]
  (when-let [code (cm-get-code editor-id)]
    (let [p (when-not (str/blank? (str path)) path)
          rp (when-not (str/blank? (str res-path)) res-path)]
      (clj {:timeout 1000} 'reval.namespace.store/save-code {:code code :path p :res-path rp}))))

(defn current-expression [editor-id]
  (when-let [c (get-editor editor-id)]
    (let [p (api/cursor-position c)
          {:keys [line col]} p
          code (api/get-code c)
          cur-exp (block-for code [line col])]
      (second cur-exp))))

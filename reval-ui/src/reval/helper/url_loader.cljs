(ns reval.helper.url-loader
  (:require
   [reagent.core :as r]
   [promesa.core :as p]
   [taoensso.timbre :refer [debug info warnf error]]
   [pinkie.ui.core :refer [error-boundary]]
   [goldly.service.core :refer [clj]]
   [pinkgorilla.repl.cljs.http :refer [get-str get-edn]]))

(def show-loader-debug-ui false)

; (get-edn "/r/repl/bongo.edn" state [:data])

;http://localhost:8000/api/viewer/file/demo.playground.cljplot/1.txt

(defn clj->atom [a fun args]
  (let [args (or args [])
        rp (apply clj {:timeout 120000} fun args)]
    (-> rp
        (p/then (fn [res]
                  (println "clj success: " res)
                  (swap! a assoc :data res)))
        (p/catch (fn [err]
                   (println "clj error: " err))))
    nil))

(defn load-url [fmt url a arg-fetch args-fetch]
  (let [comparator? (or url arg-fetch args-fetch)
        comparator [url arg-fetch args-fetch]]
    (if comparator?
      (when (not (= comparator (:comparator @a)))
        (info "url-loading fmt:" fmt "url: " url)
        (swap! a assoc :comparator comparator)
        (case fmt
          :txt (get-str url a [:data])
          :edn (get-edn url a [:data])
          :clj (if arg-fetch
                 (clj->atom a url arg-fetch)
                 (if args-fetch
                   (clj->atom a url args-fetch)
                   (clj->atom a url []))))
        nil)
      (swap! a assoc :data nil))))

; run-a is not yet perfect. It is difficult to pass args as aparameter
; (run-a state [:version] :goldly/version "goldly")

(defn debug-loader [url data args-render]
  [:div.bg-gray-500.mt-5
   [:p.font-bold "loader debug ui"]
   [:p "url: " url]
   [:p "args-render: " (pr-str args-render)]
   [:p "data: " data]])

(defn url-loader [{:keys [_url _fmt _arg-fetch _args-fetch _args-render]}
                  _fun]
  (let [a (r/atom {:data nil
                   :url nil
                   :arg-fetch nil})]
    (fn [{:keys [url fmt arg-fetch args-fetch args-render]
          :or {fmt :txt
               arg-fetch nil
               args-fetch nil
               args-render []}}
         fun]
      (load-url fmt url a arg-fetch args-fetch)
      (if-let [d (:data @a)]
        [:div
         [error-boundary
          (if (empty? args-render)
            (fun d)
            (apply fun d args-render))]
         (when show-loader-debug-ui
           [debug-loader url d args-render])]
        [:div "loading: " url]))))

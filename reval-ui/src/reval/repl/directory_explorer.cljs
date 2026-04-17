(ns reval.repl.directory-explorer
  "Directory tree for notebook sources (inspired by fnedit tree_view.cljs)."
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [clj-service.http :refer [clj]]
   [taoensso.timbre :refer [error]]
   [promesa.core :as p]))

; idea came from 
;https://github.com/nilpunning/fnedit/blob/master/src-cljs/ui/tree_view.cljs

(defn- padded [depth & body]
  [:div {:style {:padding-left (str (+ 6 (* 10 depth)) "px")
                 :padding-top "3px"
                 :padding-bottom "3px"
                 :white-space "nowrap"}}
   (into [:<>] body)])

(defn- local? [path]
  (boolean (and path (not (str/blank? (str path))))))

(defn- label-color [path]
  (if (local? path) "#16a34a" "#2563eb"))

(defn- dir-label [open? name path on-toggle]
  [:span {:style {:cursor "pointer" :color (label-color path)}
          :on-click (fn [e] (.stopPropagation e) (on-toggle))}
   [:span {:style {:display "inline-block" :width "14px"}} (if open? "▼" "▶")]
   [:span {:style {:padding-left "4px"}} name]])

(defn- file-label [name path on-open]
  [:span {:style {:cursor "pointer" :color (label-color path)}
          :on-click (fn [e] (.stopPropagation e) (on-open))}
   name])

(def ^:private active-file-bg "#e4e4e7")

;; Survives viewer2 remounts when :active-res-path (or other :data) changes — do not use a per-mount atom.
(defonce ^:private explorer-open-dirs (r/atom #{}))

(defn- tree-node [depth link-fn active-res-path {:keys [dir? children name name-full path] :as node}]
  (if dir?
    (let [open? (@explorer-open-dirs name-full)]
      [:div {:style {:color "#374151"}}
       [padded depth
        [dir-label open? name path #(swap! explorer-open-dirs (fn [s] (if (s name-full) (disj s name-full) (conj s name-full))))]]
       (when open?
         (into [:<>]
               (map (fn [ch]
                      ^{:key (or (:name-full ch) (:name ch))}
                      [tree-node (inc depth) link-fn active-res-path ch])
                    children)))])
    (let [active? (and (not (str/blank? active-res-path))
                       (= name-full active-res-path))]
      [:div {:style {:padding-left (str (+ 6 (* 10 depth)) "px")
                     :padding-top "3px"
                     :padding-bottom "3px"
                     :padding-right "6px"
                     :white-space "nowrap"
                     :background (when active? active-file-bg)
                     :border-radius "4px"
                     :margin-right "4px"}}
       [file-label name path #(link-fn node)]])))

(defn explorer-roots [{:keys [link data active-res-path]}]
  [:div {:style {:background "#f9fafb"
                 :height "100%"
                 :max-height "100%"
                 :overflow "auto"
                 :font-size "13px"}}
   (for [root (:roots data)]
     ^{:key (:res-path root)}
     [tree-node 0 link active-res-path (:tree root)])])

(defonce data-a (r/atom nil))

(->  (clj {:timeout 5000} 'reval.namespace.explore/repl-tree)
     (p/then (fn [data]
               (reset! data-a data)))
     (p/catch (fn [err]
                (error "could not load repl tree: " err)
                (reset! data-a nil))))

(defn directory-explorer-ui [{:keys [link active-res-path] :as opts}]
  (if @data-a
    [explorer-roots (assoc opts :data @data-a)]
    [:p "loading..."]))
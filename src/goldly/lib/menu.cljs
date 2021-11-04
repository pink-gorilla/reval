

(defn link-fn [fun text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-600.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn devtools-menu []
  [:div
   [link-dispatch [:bidi/goto :viewer :query-params {}] "notebook viewer"]
   [link-dispatch [:bidi/goto :scratchpad] "scratchpad"]
   [link-dispatch [:bidi/goto :environment] "environment"]
   [link-dispatch [:bidi/goto :pages] "pages"]
   [link-dispatch [:bidi/goto :devtools] "devtools help"]])

(def header
  [{:brand "Your Application"
    :brand-link "/"
    :items [{:text "goldly" :link "/goldly/about"}

            {:text "status" :link "/goldly/status"}
            {:text "theme" :link "/goldly/theme"}

            {:text "repl" :link "/repl"}
                                 ;{:text "notebooks" :link "/goldly/notebooks"}
            {:text "nrepl" :link "/goldly/nrepl"}

            {:text "snippets" :link "/system/snippet-registry"}
            {:text "running systems" :link "/goldly/systems"}

                 ;{:text "notebook" :link "/notebook-test"}
            {:text "feedback" :link "https://github.com/pink-gorilla/goldly/issues" :special? true}]}])

[site/main-with-header
 [:div "header"] 30
 [site/sidebar-layout]]
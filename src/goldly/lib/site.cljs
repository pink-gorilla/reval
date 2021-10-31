

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
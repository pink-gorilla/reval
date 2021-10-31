# reval [![GitHub Actions status |pink-gorilla/reval](https://github.com/pink-gorilla/reval/workflows/CI/badge.svg)](https://github.com/pink-gorilla/reval/actions?workflow=CI)[![Codecov Project](https://codecov.io/gh/pink-gorilla/reval/branch/master/graph/badge.svg)](https://codecov.io/gh/pink-gorilla/reval)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/reval.svg)](https://clojars.org/org.pinkgorilla/reval)

## reval
- reval stands for reproduceable [namespace=notebook] evaluation
- an eval result can be just the normal value, or it can be converted to hiccup
- our hiccup format has a little extra: it can include custom types.
  Of course custom types need special browser rendering code, but we ship that too.

## DEMO - Get Started
- clone this repo
- Run `clj -X:goldlyb`. Open Browser on Port 8000
- Open the repo in your preferred ide. Connect to nrepl on port 8000.
- demo/demo/scratchpad.clj or Eval demo/demo/notebook.clj 

## Use it in your project
- add a dependency to pinkgorilla/goldlyb.
- create a goldly config similar to `demo/goldly-reval.edn`
- Add this to your deps.edn
```
 :goldlyb
  {:extra-paths ["demo" "test"] ; to show static files (not auto generated ones)
   :extra-deps {org.pinkgorilla/goldly-bundel {:mvn/version "0.3.45"
                                               :exclusions [org.pinkgorilla/ui-site]}
                org.pinkgorilla/ui-site {:mvn/version "0.0.12"}}
   :exec-fn goldly-server.app/goldly-server-run!
   :exec-args {:profile "jetty"
               :config "demo/goldly-reval.edn"}}
```
- Now you can use your custom project in the same way as before, but get vizualisations.

## reproduceable storage

  *Example*

  Lets evaluate two namespaces:
  ```
    (eval-notebook "demo.notebook.apple)
    (eval-notebook "demo.notebook.banana)
  ```

  Now say demo.notebook.banana includes a BufferedImage, then upon 
  then the reproduceable document folder will look like this

  ```
     rdocument/demo/notebook/apple/notebook.edn
     rdocument/demo/notebook/banana/notebook.edn
     rdocument/demo/notebook/banana/67770344-1424-4803-a9aa-01e21cb4ce39.png

  ``` 
## why notebook ?
- clj cannot be evaled in the browser
- eval takes time
- eval might need extra dependencies or data 
- recalculate periodically a report that can be easily vizualised.
- documentation
- examples

## scratchpad
- you can send vizualisations of your clj expressions to the scratchpad 
- `->scratchpad` sends the vizualisation to the browser.



# For Developers

``
clj -M:test
./scripts/test-cljs.sh
```





# daniel
- in different demo github projects evaluate the notebooks.
- store the reproduceable document format of the different notebooks in scicloj/datascience-demo-notebooks
  this project then gets included by default into goldly. 
  goldly will magically find them via collection exporer
- notebook analyzer: find list of ui forms, and store them as index.
  then we use exactly that in ui. 

   
## what could be notespace
- dynamic
- instead of processing entire ns, we could only render the result of a single fn.
- then we have full control over the data.
- then code would be just the src INSIDE one function. this code can be read via rewrite-clj 
- what is important: it shudl not matter if the namespace was used or if code inside a fn was used.
- SERVE COMPLEX DOCUMENT DYNAMICALLY IN A WEB APP! URL ROUTE => WHICH FORMAT TO LOAD.

## notebook as independent html
- when a notebook is statically rendered, it needs to use renderers, otherwise we could just write html.
- therefore goldly needs to be used, because goldly has the extension manager.
- goldly needs to be stripped completley by anything eval related.
- all the goldly ui needs to be moved to reval
- to render ->hiccup must be called, with some data from the reproduceable document manager
- so webly needs to be stripped of any default ui. 
- does it make sense? NO: notebooks only make sense when you view them as a collection!
- but user must be able to start notebook viewer REALLY easy. perhaps: goldly that only starts notebook explorer?



 
cljc:
(defn notebook [nb]
  ; nb format to hiccup
)

cljs:
(defn show-hiccup [h]
  ; shows hiccup in browser.
  ; do pinkie tag replacement.
  ; input: [:vega vega-spec]
  ; output: [vega-fn vega-spec]
)

(defn notebook-page []
   ; get lists of notebooks
   
   ; show notebook
      ; -> get-edn (rdoc/link ns "notebook")

      
)







## chain of data

(defn calculate-notebook [nb-ns]
  (-> nbns
      
  )


)
clj-ns -> [get-ns-forms-as-src] (seq src) -> [eval-src] (seq of eval-result) 







s

demo.apple.nb.clj
 => is this clojure ns a notebook?
 => should this namespace be shown in a notebook list?


WE HAVE:
- namespaces
- reproduceable document manager repository
  => evalauated namespaces.


(defn demo-notebooks []
  (-> all namespaces
      (filter (str/starts-with "demo.")))
  
(defn user-notebooks []
  (-> all namespaces
      (filter (str/starts-with "test-notebook.")))
  
  (defn my-collection []
    ["demo.apple"]
    )
  

  (:p/notebook)

    fn => pinkie-hiccup => atom => lister => websocket => browser.
   
    fn => pinkie-hiccup
      SRC-CODE OUT ERR
  
(ns algo.demo)
  
(defn show-correlation-table []
  
  )

  [:div [:h1 "correlation table"]
      (algo.correlation/table ["MSFT" "GOOG" "AAPL"])
      (code algo.demo/show-correlation-table)
        (with-code 
         )
  ]

  

     [:p/code ]
   
   ]


von
goldly: rewrite, explore
picasso: kernel, nb executor
trateg: persist.

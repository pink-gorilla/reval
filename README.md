# reval [![GitHub Actions status |pink-gorilla/reval](https://github.com/pink-gorilla/reval/workflows/CI/badge.svg)](https://github.com/pink-gorilla/reval/actions?workflow=CI)[![Codecov Project](https://codecov.io/gh/pink-gorilla/reval/branch/master/graph/badge.svg)](https://codecov.io/gh/pink-gorilla/reval)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/reval.svg)](https://clojars.org/org.pinkgorilla/reval)

## reval
- reval stands for reproduceable [namespace=notebook] evaluation
- an eval result can be just the normal value, or it can be converted to hiccup
- our hiccup format has a little extra: it can include custom types.
  Of course custom types need special browser rendering code, but we ship that too.
- When you eval a form that say has a buffered image, then this form will be stored
  as a png image. Now thi happens in the context of the notebook (a notebook is
  just an evaluated namespace). 

  Example:

  If you have the following clj namespaces:

  ```
     demo.notebook.apple
     demo.notebook.banana
  ```

  Now say demo.notebook.banana includes a BufferedImage, then upon 
  ```
    (eval-notebook "demo.notebook.apple)
    (eval-notebook "demo.notebook.banana)
  ```

   then the reproduceable document folder will look like this

  ```
     rdocument/demo/notebook/apple/notebook.edn
     rdocument/demo/notebook/banana/notebook.edn
     rdocument/demo/notebook/banana/67770344-1424-4803-a9aa-01e21cb4ce39.png

  ``` 

  )
- you can evaluate a namespace
- this means you can evaluate a clj namespace, and the evaluation output is stored in
  a repository. 
- `(eval-notebook "demo.notebook.apple")` evaluates the namespace demo.notebook.apple
  and saves the output 

ui-vega defines a reagent wrapper to render vega-plots
- vega is a browser based plot renderer, that uses declarative syntax to build plots
- vega comes as vega spec and vega-lite spec. vega lite spec is compiled to vega-spec 
and is a more condensed specification with less features.





# scratchpad
- the scratchpad is all I need for development
- but it is pain in ass to always have to call ->scratchpad
- nrepl middleware can watch expression evals and send them to scratchpad
- ide specific plugin like CTRL+ENTER in vscode could do not only eval, but eval+to-hiccup+->scratchpad
- => ->scratchpad is a CORE api interface (rest or ws)
- to-hiccup is the core fn in the browser to render stuff.

# why notebook ?
- clj cannot be evaled in the browser
- eval takes time
- extra dependencies not in goldly, extra data not in goldly (say a big database)
- a notebook can be used as documentation, or to display result of batch runs that calculate something
- should NOT include too much data, in any case it should not contain data that is not renderable on browser.

# what is notespace
 (fn [ns]
   (->> ns
       (load-ns src)
       (src->src-single-form)
       (map eval-src)
       (map to-hiccup) 
       (ns->document) ;based on meta-data does something
     ))

# what could be notespace
- dynamic
- instead of processing entire ns, we could only render the result of a single fn.
- then we have full control over the data.
- then code would be just the src INSIDE one function. this code can be read via rewrite-clj 
- what is important: it shudl not matter if the namespace was used or if code inside a fn was used.
- SERVE COMPLEX DOCUMENT DYNAMICALLY IN A WEB APP! URL ROUTE => WHICH FORMAT TO LOAD.

# notebook as independent html
- when a notebook is statically rendered, it needs to use renderers, otherwise we could just write html.
- therefore goldly needs to be used, because goldly has the extension manager.
- goldly needs to be stripped completley by anything eval related.
- all the goldly ui needs to be moved to reval
- to render ->hiccup must be called, with some data from the reproduceable document manager
- so webly needs to be stripped of any default ui. 
- does it make sense? NO: notebooks only make sense when you view them as a collection!
- but user must be able to start notebook viewer REALLY easy. perhaps: goldly that only starts notebook explorer?



# daniel
- in different demo github projects evaluate the notebooks.
- store the reproduceable document format of the different notebooks in scicloj/datascience-demo-notebooks
  this project then gets included by default into goldly. 
  goldly will magically find them via collection exporer
- notebook analyzer: find list of ui forms, and store them as index.
  then we use exactly that in ui. 

   
 
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



## Demos  (port : 8000)

Run `clj -X:goldly` to see ui-vega goldly snippets. Navigate to snippets registry.

Run `clj -X:notebook watch` to edit example notebooks.



## Unit test

```
clj -M:test
./scripts/test-cljs.sh
```

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

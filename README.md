# reval [![GitHub Actions status |pink-gorilla/reval](https://github.com/pink-gorilla/reval/workflows/CI/badge.svg)](https://github.com/pink-gorilla/reval/actions?workflow=CI)[![Codecov Project](https://codecov.io/gh/pink-gorilla/reval/branch/master/graph/badge.svg)](https://codecov.io/gh/pink-gorilla/reval)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/reval.svg)](https://clojars.org/org.pinkgorilla/reval)

## reval
- reval stands for reproduceable [namespace=notebook] evaluation
- an eval result can be just the normal value, or it can be converted to hiccup
- our hiccup format has a little extra: it can include custom types.
  Of course custom types need special browser rendering code, but we ship that too.

## DEMO - Get Started
- clone this repo
- Run in directory *demo*:
```
    clj -X:goldly:build :profile '"npm-install"'
    clj -X:goldly:build :profile '"compile2"'
    clj -X:goldly
```
  Open Browser on Port 8080


## scratchpad
- you can send vizualisations of your clj expressions to the scratchpad 
- `->scratchpad` sends the vizualisation to the browser.
- Open the repo in your preferred ide. Connect to nrepl on port 9100.
- demo/demo/scratchpad.clj or Eval demo/demo/notebook.clj 

## configuration

The devtools config we use (in goldly-docs)

```
:devtools {:rdocument  {:storage-root "demo/rdocument/"
                         :url-root "/api/rdocument/file/"}
            :collections {:user [:clj "user/notebook/"]
                          :demo [:clj "demo/notebook/"]}}
```

```
(reval.config/set-config!
 {:storage-root "demo/rdocument/"
  :url-root "/api/rdocument/file/"})
```

By default storage root is "/tmp/rdocument/".


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
## why use a notebook ?

- clj cannot be evaled in the browser
- eval takes time
- eval might need extra dependencies or data 
- recalculate periodically a report that can be easily vizualised.
- documentation
- examples

# For Developers

```
clj -M:test
```

If some of the types cannot be found do `rm .cpcache -r`. Multimethods and
protocols sometimes are a little tricky.




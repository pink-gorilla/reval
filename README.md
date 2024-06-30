# reval [![GitHub Actions status |pink-gorilla/reval](https://github.com/pink-gorilla/reval/workflows/CI/badge.svg)](https://github.com/pink-gorilla/reval/actions?workflow=CI)[![Codecov Project](https://codecov.io/gh/pink-gorilla/reval/branch/master/graph/badge.svg)](https://codecov.io/gh/pink-gorilla/reval)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/reval.svg)](https://clojars.org/org.pinkgorilla/reval)

## reval
- reval stands for reproduceable [namespace=notebook] evaluation
- an eval result can be just the normal value, or it can be converted to hiccup
- our hiccup format has a little extra: it can include custom types.
  Of course custom types need special browser rendering code, but we ship that too.

## DEMO - Get Started
- clone this repo
```
    cd demo
          
    clj -X:webly:npm-install
    clj -X:webly:compile
    clj -X:webly:run
```
  Open Browser on Port 8080

A simple notebook viewer is on [Reval Github Pages](https://pink-gorilla.github.io/reval/)

## configuration

The devtools config we use (in goldly-docs)

```
:devtools {:rdocument  {:storage-root "demo/rdocument/"
                         :url-root "/api/rdocument/file/"}
            :collections {:user [:clj "user/notebook/"]
                          :demo [:clj "demo/notebook/"]}}
```

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




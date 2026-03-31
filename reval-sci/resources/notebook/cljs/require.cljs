(ns notebook.cljs.require)


(require '[goldly.sci :refer [resolve-symbol-sci]])

(def x (resolve-symbol-sci 'notebook.test27.cljs/a))

(resolve 'notebook.test27.cljs/a)

(require '[notebook.test27.cljs])

(println x)



(ns demo.frepl
  (:require
   [clojure.string :as string]
   [modular.system]
   [reval.document.notebook]
   [reval.kernel.protocol :refer [kernel-eval]]
   [promesa.core :as p]))

  ;; first lets get the running reval  instance
(def s (modular.system/system :reval))

(def code
  (reval.document.notebook/load-src "notebook.study.movies" :clj))

code

(p/await (kernel-eval {:kernel :clj
                       :ns "notebook.study.movies"
                       :code code}))


(p/await (kernel-eval {:kernel :clj
                       :ns "user"
                       :code code}))


 (p/await (kernel-eval {:code "(println 3) (def x 777) (defn f [] 99) (+ 3 4)"
              :kernel :clj
              :ns "bongotrott"
              :id 1}))

(defn add-ns [ns]
  (str "(in-ns '" ns ")\r\n"))

(def code-ns (add-ns "notebook.study.movies"))


code-ns
(p/await (kernel-eval {:kernel :clj
                       :ns "user"
                       :code code-ns}))




(load-string code)

(def code2
  (let [ns "notebook.study.movies"
        code-ns (if (and ns (not (string/blank? ns)))
                  (str "(in-ns '" ns " ) ")
                  "nil")
        code-with-ns (str code-ns " [ " code " (str *ns*) ]")]
    code-with-ns))

code2

(load-string code2)


(def code3
  (let [ns "notebook.study.movies"
        code-ns (if (and ns (not (string/blank? ns)))
                  (str "(in-ns '" ns " ) ")
                  "nil")
        code-with-ns (str code-ns code )]
    code-with-ns))

code3

(load-string code3)

(load-string "(in-ns 'xxx) (def a 1)")


(load-string "(in-ns 'xxx)  *ns*")

(load-string "(ns abc)(def x 777) (defn f [] 99) (+ 3 4)")

(load-string "(in-ns 'abc)(def x 777) (defn f [] 99) (+ 3 4)")

(load-string "(in-ns 'ddd)(def x 777) (defn f [] 99) (+ 3 4)")

(load-string "(in-ns 'ddd)(def x 777)")

(load-string "(in-ns 'ddd)(def x 777) (defn y [] 4)")

(load-string "(ns xxx)  *ns*")


(load-string "*ns*")

(load-string "(ns yyy) *ns*")



(p/await (kernel-eval {:kernel :clj
                       :ns "notebook.study.movies"
                       :code "*ns*"}))

(p/await (kernel-eval {:kernel :clj
                       :ns "notebook.study.movies"
                       :code code-with-ns}))


(def code2 "(ns bongo) (def x 34) (+ x 4) *ns*")


(p/await (kernel-eval {:kernel :clj
                         ;:ns "user"
                       :code code2}))


(p/await (kernel-eval {:kernel :clj
                       :ns "bongo"
                       :code "*ns*"}))


(load-string "*ns*")

(load-string "(ns bongo) (def y 36)")

(load-string "(in-ns 'bongo) y")


; (in-ns 'yippie2)
(load-string "(ns yippie7 (:require [clojure.pprint :refer [print-table]]))
               (def movies [{:a 1} {:a 2}])
               (print-table movies)
               *ns*
               ")

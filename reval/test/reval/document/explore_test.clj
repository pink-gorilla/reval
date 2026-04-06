(ns reval.document.explore-test
  (:require
   [clojure.test :refer [deftest is]]
   [reval.namespace.explore :as explore]
   [reval.test-init]))

(defn- strip-paths [node]
  (if (:dir? node)
    (-> node
        (dissoc :path)
        (update :children (partial mapv strip-paths)))
    (dissoc node :path)))

(deftest name-full->nbns-test
  (is (= "notebook.study.movies" (explore/name-full->nbns "notebook/study/movies.clj")))
  (is (= "foo.bar-baz" (explore/name-full->nbns "foo/bar_baz.cljc"))))

(deftest notebook-file?-test
  (is (explore/notebook-file? "a.clj"))
  (is (explore/notebook-file? "b.cljc"))
  (is (not (explore/notebook-file? "c.cljs")))
  (is (not (explore/notebook-file? "readme.txt"))))

(deftest build-tree-test
  (let [t (strip-paths (explore/build-tree "explore-test-root"))
        nested (first (filter #(= "nested" (:name %)) (:children t)))
        inner (first (filter #(not (:dir? %)) (:children nested)))]
    (is (= "explore-test-root" (:name t)))
    (is (= "explore-test-root" (:name-full t)))
    (is (true? (:dir? t)))
    (is (vector? (:children t)))
    (is (= #{"nested" "root.clj"} (set (map :name (:children t)))))
    (is (= "nested" (:name nested)))
    (is (= "inner.cljc" (:name inner)))
    (is (= "explore-test-root/nested/inner.cljc" (:name-full inner)))
    (is (= "cljc" (:ext inner)))
    (is (= "explore-test-root.nested.inner" (:nbns inner)))))

(deftest namespace-explorer-edn-test
  (let [e (explore/namespace-explorer-edn ["explore-test-root" "missing-root-xyz"])]
    (is (= ["explore-test-root" "missing-root-xyz"] (:namespace-roots e)))
    (is (= 2 (count (:roots e))))
    (is (= "explore-test-root" (:id (first (:roots e)))))
    (is (= "missing-root-xyz" (:id (second (:roots e)))))
    (is (empty? (-> e :roots second :tree :children)))))

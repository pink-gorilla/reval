(ns reval.save-test
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [reval.namespace.store :as save]
   [reval.notebook :as notebook]
   [reval.test-init]))

(deftest clone-file-path-test
  (testing "mirrors resource segments under clones root"
    (binding [save/*clones-root* "/tmp/clones"]
      (is (= "/tmp/clones/demo/notebook/foo.clj"
             (save/clone-file-path "demo/notebook/foo.clj")))
      (is (= "/tmp/clones/a.clj"
             (save/clone-file-path "a.clj")))
      (is (= "/tmp/clones/x/y.cljc"
             (save/clone-file-path "/x/y.cljc"))))))

(deftest resolve-save-target-test
  (testing "local path wins over res-path"
    (is (= {:target "/abs/local.clj" :clone? false}
           (save/resolve-save-target {:path "/abs/local.clj"
                                      :res-path "demo/x.clj"}))))
  (testing "blank path falls back to clone"
    (binding [save/*clones-root* "/c"]
      (is (= {:target "/c/nested/file.clj" :clone? true}
             (save/resolve-save-target {:path ""
                                        :res-path "nested/file.clj"})))))
  (testing "nil path uses clone"
    (binding [save/*clones-root* "/c"]
      (is (= {:target "/c/a.clj" :clone? true}
             (save/resolve-save-target {:path nil :res-path "a.clj"}))))))

(deftest slurp-clone-if-present-test
  (testing "nil when clone missing"
    (binding [save/*clones-root* (str (io/file (System/getProperty "java.io.tmpdir")
                                              (str "empty-clone-" (random-uuid))))]
      (is (nil? (save/slurp-clone-if-present "z/missing.clj"))))))

(deftest load-src-clone-overrides-classpath-test
  (testing "clone file wins over classpath resource"
    (let [root (str (io/file (System/getProperty "java.io.tmpdir")
                             (str "clone-prio-" (random-uuid))))
          rp "explore-test-root/root.clj"
          marker ";; CLONE-OVERRIDE-PRIORITY\n"]
      (try
        (binding [save/*clones-root* root]
          (fs/create-dirs (str root "/explore-test-root"))
          (spit (io/file root "explore-test-root" "root.clj") marker)
          (is (str/includes? (notebook/load-src-by-res-path rp) "CLONE-OVERRIDE-PRIORITY"))
          (is (not (str/includes? (notebook/load-src-by-res-path rp) "fixture for reval.document.explore"))))
        (finally
          (when (fs/exists? root)
            (fs/delete-tree root {:force true})))))))

(deftest save-code-integration-test
  (testing "writes clone tree for resource-only save"
    (let [root (str (io/file (System/getProperty "java.io.tmpdir")
                             (str "reval-clone-test-" (random-uuid))))]
      (try
        (binding [save/*clones-root* root]
          (save/save-code {:code "(ns cloned)\n"
                           :path nil
                           :res-path "demo/notebook/patch.clj"}))
        (is (= "(ns cloned)\n"
               (slurp (io/file root "demo/notebook/patch.clj"))))
        (finally
          (when (fs/exists? root)
            (fs/delete-tree root {:force true})))))))

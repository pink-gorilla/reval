


#_(def ex-msg
    {:session "7362f58e-b613-46f8-853c-efcd8b61a590"
     :id "d5d598e3-ec20-4bf1-b6b9-af6329ac58c1"
     :err "Execution error (IllegalArgumentException) at clojure.core.async.impl.protocols/eval88015$fn$G (protocols.clj:43).\nNo implementation of method: :exec of protocol: #'clojure.core.async.impl.protocols/Executor found for class: clojure.core.async.impl.exec.threadpool$thread_pool_executor$reify__933\n"})

#_(def ex-root-ex-msg
    {:session "7362f58e-b613-46f8-853c-efcd8b61a590"
     :id "d5d598e3-ec20-4bf1-b6b9-af6329ac58c1"
     :status #{:eval-error}
     :ex "class clojure.lang.Compiler$CompilerException"
     :root-ex "class clojure.lang.Compiler$CompilerException"})

#_(def stacktrace-msg
    {:id "b1a8e582-57ba-4cdc-a17b-5a608776bedc"
     :session "d7cecfd8-b1e0-44d7-ae9c-1d77e27f3ccc"
     :class "java.lang.Exception"
     :message "Some text"
     :stacktrace
     '({:fn "eval64540", :method "invokeStatic", :ns "user", :name "user$eval64540/invokeStatic", :file "NO_SOURCE_FILE", :type :clj, :file-url nil, :line 1, :var "user/eval64540", :class "user$eval64540", :flags #{:repl :clj}}
       {:fn "eval64540", :method "invoke", :ns "user", :name "user$eval64540/invoke", :file "NO_SOURCE_FILE", :type :clj, :file-url nil, :line 1, :var "user/eval64540", :class "user$eval64540", :flags #{:dup :repl :clj}}
       {:name "clojure.lang.Compiler/eval", :file "Compiler.java", :line 7177, :class "clojure.lang.Compiler", :method "eval", :type :java, :flags #{:tooling :java}, :file-url nil}
       {:name "clojure.lang.Compiler/eval", :file "Compiler.java", :line 7132, :class "clojure.lang.Compiler", :method "eval", :type :java, :flags #{:dup :tooling :java}, :file-url nil}
       {:fn "eval", :method "invokeStatic", :ns "clojure.core", :name "clojure.core$eval/invokeStatic", :file "core.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/org/clojure/clojure/1.10.1/clojure-1.10.1.jar!/clojure/core.clj", :line 3214, :var "clojure.core/eval", :class "clojure.core$eval", :flags #{:clj}}
       {:fn "eval", :method "invoke", :ns "clojure.core", :name "clojure.core$eval/invoke", :file "core.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/org/clojure/clojure/1.10.1/clojure-1.10.1.jar!/clojure/core.clj", :line 3210, :var "clojure.core/eval", :class "clojure.core$eval", :flags #{:clj}}
       {:fn "repl/read-eval-print/fn", :method "invoke", :ns "clojure.main", :name "clojure.main$repl$read_eval_print__9086$fn__9089/invoke", :file "main.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/org/clojure/clojure/1.10.1/clojure-1.10.1.jar!/clojure/main.clj", :line 437, :var "clojure.main/repl", :class "clojure.main$repl$read_eval_print__9086$fn__9089", :flags #{:clj}}
       {:fn "repl/read-eval-print", :method "invoke", :ns "clojure.main", :name "clojure.main$repl$read_eval_print__9086/invoke", :file "main.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/org/clojure/clojure/1.10.1/clojure-1.10.1.jar!/clojure/main.clj", :line 437, :var "clojure.main/repl", :class "clojure.main$repl$read_eval_print__9086", :flags #{:dup :clj}}
       {:fn "repl/fn", :method "invoke", :ns "clojure.main", :name "clojure.main$repl$fn__9095/invoke", :file "main.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/org/clojure/clojure/1.10.1/clojure-1.10.1.jar!/clojure/main.clj", :line 458, :var "clojure.main/repl", :class "clojure.main$repl$fn__9095", :flags #{:clj}}
       {:fn "repl", :method "invokeStatic", :ns "clojure.main", :name "clojure.main$repl/invokeStatic", :file "main.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/org/clojure/clojure/1.10.1/clojure-1.10.1.jar!/clojure/main.clj", :line 458, :var "clojure.main/repl", :class "clojure.main$repl", :flags #{:dup :clj}}
       {:fn "repl", :method "doInvoke", :ns "clojure.main", :name "clojure.main$repl/doInvoke", :file "main.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/org/clojure/clojure/1.10.1/clojure-1.10.1.jar!/clojure/main.clj", :line 368, :var "clojure.main/repl", :class "clojure.main$repl", :flags #{:clj}}
       {:name "clojure.lang.RestFn/invoke", :file "RestFn.java", :line 1523, :class "clojure.lang.RestFn", :method "invoke", :type :java, :flags #{:java}, :file-url nil} {:fn "evaluate", :method "invokeStatic", :ns "nrepl.middleware.interruptible-eval", :name "nrepl.middleware.interruptible_eval$evaluate/invokeStatic", :file "interruptible_eval.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/nrepl/nrepl/0.6.0/nrepl-0.6.0.jar!/nrepl/middleware/interruptible_eval.clj", :line 79, :var "nrepl.middleware.interruptible-eval/evaluate", :class "nrepl.middleware.interruptible_eval$evaluate", :flags #{:tooling :clj}}
       {:fn "evaluate", :method "invoke", :ns "nrepl.middleware.interruptible-eval", :name "nrepl.middleware.interruptible_eval$evaluate/invoke", :file "interruptible_eval.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/nrepl/nrepl/0.6.0/nrepl-0.6.0.jar!/nrepl/middleware/interruptible_eval.clj", :line 55, :var "nrepl.middleware.interruptible-eval/evaluate", :class "nrepl.middleware.interruptible_eval$evaluate", :flags #{:tooling :clj}}
       {:fn "interruptible-eval/fn/fn", :method "invoke", :ns "nrepl.middleware.interruptible-eval", :name "nrepl.middleware.interruptible_eval$interruptible_eval$fn__24364$fn__24368/invoke", :file "interruptible_eval.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/nrepl/nrepl/0.6.0/nrepl-0.6.0.jar!/nrepl/middleware/interruptible_eval.clj", :line 142, :var "nrepl.middleware.interruptible-eval/interruptible-eval", :class "nrepl.middleware.interruptible_eval$interruptible_eval$fn__24364$fn__24368", :flags #{:tooling :clj}}
       {:name "clojure.lang.AFn/run", :file "AFn.java", :line 22, :class "clojure.lang.AFn", :method "run", :type :java, :flags #{:java}, :file-url nil} {:fn "session-exec/main-loop/fn", :method "invoke", :ns "nrepl.middleware.session", :name "nrepl.middleware.session$session_exec$main_loop__24465$fn__24469/invoke", :file "session.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/nrepl/nrepl/0.6.0/nrepl-0.6.0.jar!/nrepl/middleware/session.clj", :line 171, :var "nrepl.middleware.session/session-exec", :class "nrepl.middleware.session$session_exec$main_loop__24465$fn__24469", :flags #{:tooling :clj}}
       {:fn "session-exec/main-loop", :method "invoke", :ns "nrepl.middleware.session", :name "nrepl.middleware.session$session_exec$main_loop__24465/invoke", :file "session.clj", :type :clj, :file-url "jar:file:/home/andreas/.m2/repository/nrepl/nrepl/0.6.0/nrepl-0.6.0.jar!/nrepl/middleware/session.clj", :line 170, :var "nrepl.middleware.session/session-exec", :class "nrepl.middleware.session$session_exec$main_loop__24465", :flags #{:tooling :clj}}
       {:name "clojure.lang.AFn/run", :file "AFn.java", :line 22, :class "clojure.lang.AFn", :method "run", :type :java, :flags #{:java}, :file-url nil} {:name "java.lang.Thread/run", :file "Thread.java", :line 748, :class "java.lang.Thread", :method "run", :type :java, :flags #{:java}, :file-url nil})})

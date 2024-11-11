 #!/bin/sh
cd demo
rm target -r
rm .shadow-cljs -r
rm .cpcache -r
rm package.json
rm package-lock.json
rm shadow-cljs.edn
rm karma.conf.js
clojure -X:webly:npm-install
clojure -X:webly:release-adv
clojure -X:webly:static
clojure -X:nbeval
cp .reval/public/rdocument target/static/r -r
echo "done"
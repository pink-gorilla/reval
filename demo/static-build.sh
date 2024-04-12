#!/bin/sh

rm target -r
rm .shadow-cljs -r
clojure -X:webly:npm-install
clojure -X:webly:release-adv
clojure -X:webly:static
clojure -X:nbeval
cp target/webly/public/rdocument target/static/r -r
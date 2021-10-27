#!/bin/sh

clojure -X:ci :profile '"npm-install"'
clojure -X:ci 
npm test
#!/bin/bash

for dir in ../configs/*/     # list directories in the form "/tmp/dirname/"
do
    dir=${dir%*/}      # remove the trailing "/"
    curl --request PUT --data-binary @../configs/${dir##*/}/application.yaml http://localhost:8500/v1/kv/config/${dir##*/}/application.yaml
done

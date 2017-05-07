#!/usr/bin/env bash

cd -P -- "$(dirname -- "$0")" && pwd -P
#echo $dir

if [ "$(uname)" == "Darwin" ]; then
	./crumbs-darwin
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
	./crumbs-linux
fi

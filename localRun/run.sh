#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd ${DIR}/../

echo "Setting environment from .envrc !"
source ${DIR}/.example_envrc
source ${DIR}/.envrc

./gradlew bootRun
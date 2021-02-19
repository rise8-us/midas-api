#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd ${DIR}/../

if [ -f "${DIR}/.envrc" ]; then
    echo "Setting environment from .envrc !"
    source ${DIR}/.envrc
else
    echo "Setting environment from .envrc.example !"
    source ${DIR}/.envrc.example
fi

./gradlew bootRun
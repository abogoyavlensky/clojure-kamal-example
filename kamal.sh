#!/bin/bash

OS=$(uname)
KAMAL_VERSION=v1.5.2

if [[ "$OS" == "Linux" ]]; then
    docker run -it --rm -v "${PWD}:/workdir" -v "${SSH_AUTH_SOCK}:/ssh-agent" -v /var/run/docker.sock:/var/run/docker.sock -e "SSH_AUTH_SOCK=/ssh-agent" ghcr.io/basecamp/kamal:${KAMAL_VERSION} "$@"
elif [[ "$OS" == "Darwin" ]]; then
    docker run -it --rm -v "${PWD}:/workdir" -v "/run/host-services/ssh-auth.sock:/run/host-services/ssh-auth.sock" -e SSH_AUTH_SOCK="/run/host-services/ssh-auth.sock" -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/basecamp/kamal:${KAMAL_VERSION} "$@"
else
    echo "Unsupported OS"
fi

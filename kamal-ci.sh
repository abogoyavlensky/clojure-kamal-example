#!/bin/bash

KAMAL_VERSION=v1.5.2
docker run --rm -v "${GITHUB_WORKSPACE}:/workdir" -v "${SSH_AUTH_SOCK}:/ssh-agent" -v /var/run/docker.sock:/var/run/docker.sock -e "SSH_AUTH_SOCK=/ssh-agent" ghcr.io/basecamp/kamal:${KAMAL_VERSION} "$@"

#!/usr/bin/env bash

set -e
set -x

echo Running all tests

export TZ=UTC

# Aim for a clean build
find . -name target | xargs rm -r || true

# Run all tests, create the deliverables
mvn clean install

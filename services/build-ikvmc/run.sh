#!/bin/bash
if [ "$#" -ne 1 ]; then
    echo "Please provide the version number as an argument, like: ./run.sh 1.1.509"
    exit 1
fi
VERSION=$1
sed -i -e '12s/.*/CMD VERSION='$VERSION' \&\& \\/' Dockerfile
echo "doing it with version $version"
docker-compose up --build

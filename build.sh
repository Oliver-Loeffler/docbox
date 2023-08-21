#!/bin/bash

cp -f ./docker/application.properties ./src/main/resources/application.properties
./mvnw package -DskipTests=True
docker build -f Dockerfile -t docdropdev:0.4 .


#!/bin/bash
rm -f ./docker/*.class
rm -f ./docker/*.jar

(cd ./docker; javac PrepareHttpd.java; jar cfe PrepareHttpd.jar PrepareHttpd PrepareHttpd.class)
(cd ./docker; javac PrepareDist.java; jar cfe PrepareDist.jar PrepareDist PrepareDist.class)

cp -f ./docker/application.properties ./src/main/resources/application.properties
# docker build -f Dockerfile -t raumzeitfalle/docbox:0.7.1 .


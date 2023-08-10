#!/bin/bash
/usr/local/bin/java /docdrop/docker/PrepareHttpd.java
/usr/sbin/httpd
# /usr/local/bin/java -jar /docdrop/target/*-runner.jar
cd /docdrop && sh ./mvnw compile quarkus:dev



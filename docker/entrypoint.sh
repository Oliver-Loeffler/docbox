#!/bin/bash
/usr/sbin/httpd
# /usr/local/bin/java -jar /docdrop/target/*-runner.jar
/usr/local/bin/java /docdrop/PrepareHttpd.java
cd /docdrop && sh ./mvnw compile quarkus:dev



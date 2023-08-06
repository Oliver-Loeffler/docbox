#!/bin/bash
/usr/sbin/php-fpm
/usr/sbin/httpd
# /usr/local/bin/java -jar /docdrop/target/*-runner.jar
cd /docdrop && sh ./mvnw compile quarkus:dev


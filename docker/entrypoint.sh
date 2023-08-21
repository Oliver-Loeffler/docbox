#!/bin/bash
/usr/local/bin/java /docdrop/PrepareHttpd.java
/usr/sbin/httpd
/usr/local/bin/java -jar /docdrop/quarkus-run.jar

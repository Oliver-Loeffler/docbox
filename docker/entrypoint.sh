#!/bin/bash
/usr/local/bin/java /docbox/PrepareHttpd.java
/usr/sbin/httpd
/usr/local/bin/java -jar /docbox/quarkus-run.jar

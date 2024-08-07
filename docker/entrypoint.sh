#!/bin/bash
/usr/local/bin/java -jar /docbox/PrepareHttpd.jar docker
/usr/local/bin/java -jar /docbox/PrepareDist.jar docker
/usr/sbin/httpd
/usr/local/bin/java -jar /docbox/quarkus-run.jar

#!/bin/bash
/usr/local/bin/java /docbox/PrepareHttpd.java docker
/usr/local/bin/java /docbox/PrepareDist.java docker
/usr/sbin/httpd
/usr/local/bin/java -jar /docbox/quarkus-run.jar

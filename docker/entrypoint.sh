#!/bin/bash

# Initial Configuration Bootstrapping
/usr/local/bin/java -jar /docbox/PrepareHttpd.jar docker
/usr/local/bin/java -jar /docbox/PrepareDist.jar docker

# Launching the Services
(/usr/sbin/httpd; cd /docbox; /usr/local/bin/java -jar /docbox/quarkus-run.jar)

#!/bin/bash

# Initial Configuration Bootstrapping
/usr/local/bin/java -jar /docbox/PrepareHttpd.jar docker
/usr/local/bin/java -jar /docbox/PrepareDist.jar docker

# Generate Directories if not existing
mkdir -p /var/www/html/artifacts
mkdir -p /var/www/html/ingest

(/bin/bash /docbox/deploy_bootstrap.sh)

# Launching the Services
(/usr/sbin/httpd; cd /docbox; /usr/local/bin/java -jar /docbox/quarkus-run.jar)

#!/bin/bash

# Initial Configuration Bootstrapping
./docbox/PrepareHttpd docker
./docbox/PrepareDist docker

# Generate Directories if not existing
mkdir -p /var/www/html/artifacts
mkdir -p /var/www/html/ingest

(/bin/bash /docbox/deploy_bootstrap.sh)

# Launching the Services
(/usr/sbin/httpd; cd /docbox; ./docbox-runner)

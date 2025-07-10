FROM raumzeitfalle/graalbuilder-21.0.7/rocky-10:0.1 as builder
ADD ./src /projects/docbox/src
ADD ./pom.xml /projects/docbox/pom.xml
ADD ./docker/PrepareDist.jar /projects/tools/PrepareDist.jar
ADD ./docker/PrepareHttpd.jar /projects/tools/PrepareHttpd.jar
WORKDIR /projects/docbox
RUN mvn verify -Dnative -DskipTests=true -Dlicense.skipAddThirdParty=true -Dlicense.skipDownloadLicenses
WORKDIR /projects/tools
RUN native-image -jar PrepareDist.jar PrepareDist && \
    native-image -jar PrepareHttpd.jar PrepareHttpd

FROM rockylinux/rockylinux:10-minimal as runtime

RUN microdnf -y --refresh upgrade \
    && microdnf -y --best --nodocs --noplugins \
                install  \
                httpd  \
                tar  \
                unzip  \
    && microdnf clean all

RUN mkdir -p /docbox  \
    && mkdir -p /docbox/dist  \
    && mkdir -p /var/www/html/dist  \
    && mkdir -p /var/www/html/ingest  \
    && mkdir -p /var/www/html/artifacts  \
    && mkdir -p /var/log/docbox

RUN cd /tmp  \
    && curl -L -o bootstrap-5.3.1-dist.zip "https://github.com/twbs/bootstrap/releases/download/v5.3.1/bootstrap-5.3.1-dist.zip"  \
    && mkdir -p /var/www/html/dist  \
    && mkdir -p /var/docbox/dist \
    && unzip -o bootstrap-5.3.1-dist.zip -d /tmp  \
    && cp -r /tmp/bootstrap-5.3.1-dist /var/www/html/dist/bootstrap-5.3.1  \
    && cp -r /tmp/bootstrap-5.3.1-dist /docbox/dist/bootstrap-5.3.1 \
    && cp -r /tmp/bootstrap-5.3.1-dist /var/docbox/dist/bootstrap-5.3.1 \
    && rm -rf /tmp/bootstrap-5.3.1-dist \
    && rm -f /tmp/bootstrap-5.3.1-dist.zip

COPY ./docker/css/application.css /var/docbox/dist/application.css
COPY ./docker/css/gh-fork-ribbon.min.css /var/docbox/dist/gh-fork-ribbon.min.css
COPY ./docker/js/darkmode.js /var/docbox/dist/darkmode.js
COPY ./docker/application.properties /docbox/application.properties
COPY ./docker/httpd.conf /etc/httpd/conf/httpd.conf
COPY ./target/quarkus-app /docbox
COPY ./docker/PrepareHttpd.jar /docbox
COPY ./docker/PrepareDist.jar /docbox
COPY ./docker/deploy_bootstrap.sh /docbox

ENV DOCBOX_HOSTURL=http://localhost
ENV TEMP=/var/log/quarkus
ENV TZ=Europe/Berlin

ADD ./docker/entrypoint.sh /docbox/entrypoint.sh

COPY --from=builder /projects/tools/PrepareDist /docbox/PrepareDist
COPY --from=builder /projects/tools/PrepareHttpd /docbox/PrepareHttpd
COPY --from=builder /projects/docbox/target/docbox-0.7.1-SNAPSHOT-runner /docbox/docbox-runner

CMD ["sh", "/docbox/entrypoint.sh"]

EXPOSE 80

# VOLUME /var/www/html/dist
# VOLUME /var/www/html/ingest
# VOLUME /var/www/html/artifacts
VOLUME /var/www/html/
VOLUME /var/log/
VOLUME /docbox/


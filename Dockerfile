FROM rockylinux/rockylinux:10-minimal

RUN microdnf -y --refresh upgrade \
    && microdnf -y --best --nodocs --noplugins \
                install  \
                httpd  \
                tar  \
                unzip  \
    && microdnf clean all

ENV NODE_ENV="production"

RUN cd /tmp \
    && curl -L -o OpenJDK17U-jre_x64_linux_hotspot_17.0.12_7.tar.gz https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.12+7/OpenJDK17U-jre_x64_linux_hotspot_17.0.12_7.tar.gz \
    && tar -xf OpenJDK17U-jre_x64_linux_hotspot_17.0.12_7.tar.gz \
    && rm -f OpenJDK17U-jre_x64_linux_hotspot_17.0.12_7.tar.gz \
    && mkdir -p /usr/lib/jvm \
    && mv jdk-17.0.12+7-jre /usr/lib/jvm \
    && ln -s /usr/lib/jvm/jdk-17.0.12+7-jre/bin/java /usr/local/bin/java

ENV JAVA_HOME="/usr/lib/jvm/jdk-17.0.12+7-jre"

RUN mkdir -p /docbox  \
    && mkdir -p /docbox/dist  \
    && mkdir -p /var/www/html/dist  \
    && mkdir -p /var/www/html/ingest  \
    && mkdir -p /var/www/html/artifacts  \
    && mkdir -p /var/log/docbox

RUN cd /tmp  \
    && curl -L -o bootstrap-5.3.1-dist.zip "https://github.com/twbs/bootstrap/releases/download/v5.3.1/bootstrap-5.3.1-dist.zip"  \
    && mkdir -p /var/www/html/dist  \
    && unzip -o bootstrap-5.3.1-dist.zip -d /tmp  \
    && cp -r /tmp/bootstrap-5.3.1-dist /var/www/html/dist/bootstrap-5.3.1  \
    && cp -r /tmp/bootstrap-5.3.1-dist /docbox/dist/bootstrap-5.3.1 \
    && rm -rf /tmp/bootstrap-5.3.1-dist \
    && rm -f /tmp/bootstrap-5.3.1-dist.zip

COPY ./docker/css/application.css /docbox/dist/application.css
COPY ./docker/css/gh-fork-ribbon.min.css /docbox/dist/gh-fork-ribbon.min.css
COPY ./docker/js/darkmode.js /docbox/dist/darkmode.js
COPY ./docker/application.properties /docbox/application.properties
COPY ./docker/httpd.conf /etc/httpd/conf/httpd.conf
COPY ./target/quarkus-app /docbox
COPY ./docker/PrepareHttpd.jar /docbox
COPY ./docker/PrepareDist.jar /docbox

ENV PAGEFIND_SITE=/var/www/html/artifacts/
ENV DOCBOX_HOSTURL=http://localhost
ENV TEMP=/var/log/quarkus
ENV TZ=Europe/Berlin

ADD ./docker/entrypoint.sh /docbox/entrypoint.sh
WORKDIR /docbox

CMD ["sh", "./entrypoint.sh"]

EXPOSE 80

# VOLUME /var/www/html/dist
# VOLUME /var/www/html/ingest
# VOLUME /var/www/html/artifacts
VOLUME /var/www/html/
VOLUME /var/log/
VOLUME /docbox/


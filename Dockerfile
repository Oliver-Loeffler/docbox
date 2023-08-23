FROM rockylinux:8.8
RUN dnf upgrade --refresh -y --nogpgcheck && \
    dnf install epel-release -y && \
    dnf install \
        gcc \
        openssl-devel \
        bzip2-devel \
        zlib-devel \
        wget \
        make \
        git \
        procps \
        which \
        mysql-devel \
        libaio \
        libsqlite3x-devel \
        findutils \
        nano \
        vim \
        nano \
        httpd \
        tar \
        unzip -y

RUN mkdir install && \
    cd install && \
    wget "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8+7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.8_7.tar.gz" && \
    tar -xf OpenJDK17U-jdk_x64_linux_hotspot_17.0.8_7.tar.gz && \
    rm -f OpenJDK17U-jdk_x64_linux_hotspot_17.0.8_7.tar.gz && \
    mkdir -p /usr/lib/jvm && \
    mv jdk-17.0.8+7 /usr/lib/jvm && \
    ln -s /usr/lib/jvm/jdk-17.0.8+7/bin/java /usr/local/bin/java

RUN rm -rf /etc/localtime && \
    ln -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime

RUN mkdir -p /docbox && \
    mkdir -p /var/www/html/dist/ && \
    mkdir -p /var/www/html/ingest && \
    mkdir -p /var/www/html/artifacts && \
    mkdir -p /var/log/docbox

RUN cd install && \
    wget "https://github.com/twbs/bootstrap/releases/download/v5.3.1/bootstrap-5.3.1-dist.zip" && \
    mkdir -p /var/www/html/dist && \
    unzip -o bootstrap-5.3.1-dist.zip -d /install && \
    mv /install/bootstrap-5.3.1-dist /var/www/html/dist/bootstrap-5.3.1

ENV TZ=Europe/Berlin
ENV JAVA_HOME="/usr/lib/jvm/jdk-17.0.8+7"

COPY ./docker/css/application.css /var/www/html/dist/application.css
COPY ./docker/css/gh-fork-ribbon.min.css /var/www/html/dist/gh-fork-ribbon.min.css
COPY ./docker/js/darkmode.js /var/www/html/dist/darkmode.js
COPY ./docker/application.properties /docbox/application.properties
COPY ./docker/httpd.conf /etc/httpd/conf/httpd.conf
COPY ./target/quarkus-app /docbox
COPY ./docker/PrepareHttpd.java /docbox 

ENV DOCBOX_HOSTURL=http://localhost
ENV TEMP=/var/log/docbox

ADD ./docker/entrypoint.sh /entrypoint.sh

CMD ["sh", "./entrypoint.sh"]

EXPOSE 80

VOLUME /var/www/html/ingest/
VOLUME /var/www/html/artifacts/
VOLUME /var/www/html/dist/
VOLUME /var/log/httpd/
VOLUME /var/log/docbox/
VOLUME /docbox/


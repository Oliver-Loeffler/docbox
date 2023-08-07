FROM rockylinux:8.6
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
    wget "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.8_7.tar.gz" && \
    tar -xf OpenJDK17U-jdk_x64_linux_hotspot_17.0.8_7.tar.gz && \
    rm -f OpenJDK17U-jdk_x64_linux_hotspot_17.0.8_7.tar.gz && \
    mkdir -p /usr/lib/jvm && \
    mv jdk-17.0.8+7 /usr/lib/jvm && \
    ln -s /usr/lib/jvm/jdk-17.0.8+7/bin/java /usr/local/bin/java

RUN rm -rf /etc/localtime && \
    ln -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime

ENV TZ=Europe/Berlin
ENV JAVA_HOME="/usr/lib/jvm/jdk-17.0.8+7"

RUN  git clone https://github.com/Oliver-Loeffler/docdrop.git && \
     cd docdrop && \
     sh ./mvnw package -Dquarkus.package.type=uber-jar

RUN mkdir -p /var/www/html/dist/ && \
    mkdir -p /var/www/html/ingest && \
    mkdir -p /var/www/html/artifacts \

RUN cd install && \
    wget "https://github.com/twbs/bootstrap/releases/download/v5.3.1/bootstrap-5.3.1-dist.zip" && \
    mkdir -p /var/www/html/dist && \
    unzip -o bootstrap-5.3.1-dist.zip -d /install && \
    mv /install/bootstrap-5.3.1-dist /var/www/html/dist/bootstrap-5.3.1

ADD ./css/docdrop.css /var/www/html/dist/
ADD ./js/darkmode.js /var/www/html/dist/
ADD ./index.html /var/www/html/

COPY ./application.properties /docdrop/src/main/resources/
COPY ./httpd.conf /etc/httpd/conf/httpd.conf

ADD ./entrypoint.sh /

CMD ["sh", "./entrypoint.sh"]

EXPOSE 80
EXPOSE 8080

VOLUME /var/www/html/artifacts/
VOLUME /var/www/html/dist/
VOLUME /var/log/
VOLUME /docdrop/

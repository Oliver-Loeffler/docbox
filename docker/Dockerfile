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
        unzip \
        php -y

RUN mkdir -p /var/run/php-fpm

RUN mkdir install && \
    cd install && \
    wget "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8%2B7/OpenJDK17U-jdk_x64_linux_hotspot_17.0.8_7.tar.gz" && \
    tar -xf OpenJDK17U-jdk_x64_linux_hotspot_17.0.8_7.tar.gz && \
    rm -f OpenJDK17U-jdk_x64_linux_hotspot_17.0.8_7.tar.gz && \
    mkdir -p /usr/lib/jvm && \
    mv jdk-17.0.8+7 /usr/lib/jvm && \
    ln -s /usr/lib/jvm/jdk-17.0.8+7/bin/java /usr/local/bin/java

RUN rm -rf /etc/localtime && \
    ln -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime && \
    echo TZ="Europe/Berlin" >> /etc/environment && \
    echo TZ="Europe/Berlin" >> /etc/profile

RUN mkdir -p /src/docdrop

ADD run.sh /
ADD httpd.conf /etc/httpd/conf/httpd.conf
ADD entrypoint.sh /
ADD index.php /var/www/html/
ADD css/docdrop.css /var/www/html/dist
ADD js/darkmode.js /var/www/html/dist

ENV TZ=Europe/Berlin
ENV JAVA_HOME="/usr/lib/jvm/jdk-17.0.8+7"
CMD ["sh", "./entrypoint.sh"]
#CMD ["top"]

EXPOSE 80
EXPOSE 8080

VOLUME /var/www/html/
VOLUME /src/docdrop
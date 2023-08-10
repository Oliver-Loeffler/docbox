FROM raumzeitfalle/rocky-8.6-java17:0.1

ENV TZ=Europe/Berlin
ENV JAVA_HOME="/usr/lib/jvm/jdk-17.0.8+7"

RUN  git clone https://github.com/Oliver-Loeffler/docdrop.git && \
     cd docdrop && \
     sh ./mvnw compile -DskipTests=True

RUN mkdir -p /var/www/html/dist/ && \
    mkdir -p /var/www/html/ingest && \
    mkdir -p /var/www/html/artifacts && \
    mkdir -p /var/log/docdrop

RUN cd install && \
    wget "https://github.com/twbs/bootstrap/releases/download/v5.3.1/bootstrap-5.3.1-dist.zip" && \
    mkdir -p /var/www/html/dist && \
    unzip -o bootstrap-5.3.1-dist.zip -d /install && \
    mv /install/bootstrap-5.3.1-dist /var/www/html/dist/bootstrap-5.3.1


ADD ./css/docdrop.css /var/www/html/dist/
ADD ./js/darkmode.js /var/www/html/dist/
COPY ./application.properties /docdrop/src/main/resources/
ADD ./PrepareHttpd.java /docdrop/

ENV DOCDROP_HOSTURL=http://localhost
ENV TEMP=/var/log
ADD ./entrypoint.sh /

CMD ["sh", "./entrypoint.sh"]

EXPOSE 80
EXPOSE 8080

VOLUME /var/www/html/artifacts/
VOLUME /var/www/html/dist/
VOLUME /var/log/
VOLUME /docdrop/

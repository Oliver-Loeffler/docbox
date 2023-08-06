# Docker Image for DocDrop

## Exposed properties

**_Ports_**:

* Apache httpd running on port 80
* Quarkus dopdroc running on port 8080
* http://localhost is redirected via meta to http://localhost:8080/upload.html

**_Volumes_**:

* Artifacts: `/var/www/html/artifacts`
* CSS/JS files: `/var/www/html/dist/`
* Logfiles: 
    * Docdrop / Quarkus: `/var/log/docdrop.log` (rotating file handler, 10x 10 MiB)
    * Httpd: `/var/log/httpd/` (various logs)
* Application directory: `/docdrop`

## Mode of operation

The current image runs Quarkus still in development mode (started with: `./mvnw compile quarkus:dev`). Hence all the features like development console, live configuration etc. are available. It is started via `/entrypoint.sh` after PHP and httpd. Httpd runs in background, Quarkus-Docdrop in foreground.

Quarkus permits access from any host as it is configured with `quarkus.http.host=0.0.0.0` in its `application.properties`.

## REST endpoint for programmatic artifact uploads

The following example shows how an upload is using the multipart form format. The endpoint does not provide an answer, its simply consumes the file. There is yet also no indication in the GUI if the upload worked or not. Only files of type `.zip`, `.tar`, `.tar.gz` and `-javadoc.jar` are supported. Other files are consumed at the moment but deleted after internal validation.

```shell
curl -v -F group="net.opensource" \
        -F artifact="library" \
        -F version="v1.0.2" \
        -F file=@"c:\mylibrary-javadoc.jar" \
        http://localhost:8080/upload
```




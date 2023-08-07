# docdrop

## Sharing documentation artifacts from CI/CD

## Concept:

* Artifacts are structured by group name, artifact name, artifact version and snapshot
* `-javadoc.jar`, `.zip`, `.tar` and `.tar.gz` files are accepted
* Each artifact will be accessible via unique URL like `http://localhost/artifacts/group/artifact/version/snapshot/`
* There is a generic shortcut URL for the latest version of each artifact: `http://localhost/artifacts/group/artifact/version/latest/`
* There are index pages:
    * Group index: lists all artifacts within a group
    * Artifact index: lists all versions of an artifact within a group
    * Version index: lists all snapshots of a particular artifact version
    * An upload page: http://localhost:8080/upload.html
    * A status page: http://localhost:8080/status.html

## Setup:

* Artifacts are supposed to be served by an Apache httpd server (port 80) which respects created `.htaccess` files.
* A Quarkus web app running on port 8080 allows uploads and basic maintenance via `status.html` and `upload.html`
* The `upload.html` also describes how to publish artifacts via cURL.

## Backend

* There is no database.
* Quarkus uses either `7za.exe` on Windows or `unzip` and `tar` on Linux.
* Its all not yet tested on MacOS or other systems.
* 7Zip Extra for Windows: https://www.7-zip.org/a/7z2301-extra.7z

## Configuration

* The application is configured using Quarkus `application.properties`

Following options exist:

| Configuration key                   | Description                       | Example     |
|-------------------------------------|-----------------------------------|-------------|
| `docdrop.repository.name`           | App name                          | DocDrop     |
| `docdrop.repository.index.file`     | Name of directory index files.    | index.html  |
| `docdrop.css.bootstrap.dist.url`    | full URL of `bootstrap.css`       |             |
| `docdrop.css.url`                   | DocDrop custom CSS: `docdrop.css` |             |
| `docdrop.views.artifacts.index.url` | Artifact index root URL           | http://localhost/artifacts        |
| `docdrop.views.upload.url`          | Location of upload.html           | http://localhost:8080/upload.html |
| `docdrop.views.status.url`          | Location of status.html           | http://localhost:8080/status.html |
| `docdrop.artifact.storage.location` | Volume for artifact storage       | `C:\Temp`         |
| `docdrop.commands.7z.location`      | 7za Executable                    | `C:\Test\7za.exe` |
| `docdrop.commands.tar.location`     | TAR Executable                    | `/usr/bin/tar`    |
| `docdrop.commands.unzip.location`   | UNZIP Executable                  | `/usr/bin/unzip`  |

## Running DocDrop

**_Prerequisite_**:
* Apache httpd must be running and serving contents of `docdrop.artifact.storage.location` 

Starting the Web App:
* Change into project directory and run `./mvnw compile quarkus:dev`
* Quarkus is configured so that it also serves from within a Docker container.
* The developer UI is also available:  http://localhost:8080/q/dev/

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

There is now a first Docker image available. 

```shell
docker pull raumzeitfalle/docdrop:0.1
docker run -it --name docdrop -p 80:80 -p 8080:8080 -d raumzeitfalle/docdrop:0.2
```

When operated on a different port than 8080 for Quarkus or 80 for httpd, a configuration change is needed.
The Quarkus configuration can be changed in the developers GUI `http://localhost:8080/q/dev-ui/configuration-form-editor` (replace port 8080 accordingly).
There the settings `docdrop.views.upload.url` and `docdrop.views.status.url` require an update as well.
As the index files are just basic static files, those need to be updated as well. This can be achieved on the `http://localhost:8080/status.html` page. All index levels can be updated there automatically. Depending on the amount of artifacts stored, this may take a while. The changes will not be effective immediately but eventually the will be.

## Screenshots

### Uploading Artifacts
![upload-form](doc/images/upload-form.png)

### Group Index

![group-index](doc/images/group-index.png)

### Artifact Index

![artifact-index-1](doc/images/artifact-index-1.png)

![artifact-index-2](doc/images/artifact-index-2.png)

### Version Index

![version-index](doc/images/version-index.png)

### Index by Snapshot

![snapshot-index](doc/images/snapshot-index.png)

### Status and Maintenance

![status-form](doc/images/status-form.png)

### Video

Conveniently host Docdrop as a container and publish your own documentation artifacts either via HTML form or via POST request using cURL. Supports `.zip`, `.tar`, `.tar.gz` and `-javadoc.jar` .
Boundary condition: the artifacts shoul have an `index.html` in their root otherwise the httpd would not know what to show.

```shell
curl -v -F group="net.opensource" \
        -F artifact="library" \
        -F version="v1.0.2" \
        -F file=@"c:\mylibrary-javadoc.jar" \
        http://localhost:8080/upload
```

https://github.com/Oliver-Loeffler/docdrop/assets/22102800/1e221ef2-dc91-4c2b-a7b7-f1bea0108cc7

## Packaging the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/docdrop-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- RESTEasy Classic Multipart ([guide](https://quarkus.io/guides/rest-json#multipart-support)): Multipart support for RESTEasy Classic
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing Jakarta REST and more

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)

## Attributions

The very nice Fork-Me-At-Github ribbon is made by: Simon Whitaker

https://simonwhitaker.github.io/github-fork-ribbon-css/

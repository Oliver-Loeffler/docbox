@ECHO OFF
copy /Y .\docker\application.properties .\src\main\resources\application.properties
call .\mvnw package -DskipTests=True
docker build -f Dockerfile -t raumzeitfalle/docbox:0.5.6 .

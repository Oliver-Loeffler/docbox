@ECHO OFF
copy /Y .\docker\application.properties .\src\main\resources\application.properties
call .\mvnw package -DskipTests=True
docker build -f Dockerfile -t raumzeitfalle/docdrop:0.3 .

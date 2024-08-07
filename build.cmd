@ECHO OFF
copy /Y .\docker\application.properties .\src\main\resources\application.properties
call .\mvnw package -DskipTests=True
cd docker
del *.jar
del *.class
javac PrepareHttpd.java
jar cfe PrepareHttpd.jar PrepareHttpd PrepareHttpd.class
javac PrepareDist.java
jar cfe PrepareDist.jar PrepareDist PrepareDist.class
cd ..
docker build -f Dockerfile -t raumzeitfalle/docbox:0.6.0 .

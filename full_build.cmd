@ECHO OFF
copy /Y .\docker\application.properties .\src\main\resources\application.properties
cd docker
del *.jar
del *.class
javac PrepareHttpd.java
jar cfe PrepareHttpd.jar PrepareHttpd PrepareHttpd.class
javac PrepareDist.java
jar cfe PrepareDist.jar PrepareDist PrepareDist.class
cd ..

cd native
del pom.xml
copy ..\pom.xml .\pom.xml
call build.cmd
cd .. 
docker build -f Dockerfile -t raumzeitfalle/docbox:0.7.1 .

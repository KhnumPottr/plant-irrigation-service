ECHO Building JAR
call mvnw clean package spring-boot:repackage

ECHO Building docker container
docker build -t plant-irrigation-service .

ECHO Launching docker container
docker run -d -p 8080:8080 plant-irrigation-service

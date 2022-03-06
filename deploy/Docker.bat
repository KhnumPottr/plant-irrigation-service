ECHO Building JAR
call mvnw clean package spring-boot:repackage

ECHO Building docker container
docker container stop PlantIrrigationService
docker container rm PlantIrrigationService
docker image rm plant-irrigation-service
docker build -t plant-irrigation-service .

ECHO Launching docker container
docker run -d --name PlantIrrigationService -p 8080:8080 plant-irrigation-service

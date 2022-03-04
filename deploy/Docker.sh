mvn clean package spring-boot:repackage
docker container stop PlantIrrigationService
docker image rm plant-irrigation-service
docker build -t plant-irrigation-service ../
docker run -d --name PlantIrrigationService -p 8080:8080 plant-irrigation-service

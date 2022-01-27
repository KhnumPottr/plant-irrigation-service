mvn clean package spring-boot:repackage
docker build -t plant-irrigation-service ../
docker run -d -p 8080:8080 plant-irrigation-service

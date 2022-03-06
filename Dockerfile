FROM maven:3.6.0-jdk-11-slim AS build
COPY src src
COPY pom.xml /
RUN mvn -f pom.xml clean package

FROM openjdk:11
VOLUME /tmp
COPY --from=build ./target/plant-irrigation-service-0.0.2-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
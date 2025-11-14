FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY inventory-service/pom.xml inventory-service/
COPY inventory-service/src inventory-service/src
RUN mvn clean package -DskipTests -pl inventory-service

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/inventory-service/target/inventory-service-1.0.0.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]


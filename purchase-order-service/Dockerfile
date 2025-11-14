FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY purchase-order-service/pom.xml purchase-order-service/
COPY purchase-order-service/src purchase-order-service/src
RUN mvn clean package -DskipTests -pl purchase-order-service

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/purchase-order-service/target/purchase-order-service-1.0.0.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]


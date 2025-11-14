FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY procurement-service/pom.xml procurement-service/
COPY procurement-service/src procurement-service/src
RUN mvn clean package -DskipTests -pl procurement-service

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/procurement-service/target/procurement-service-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]


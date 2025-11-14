FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY quotation-service/pom.xml quotation-service/
COPY quotation-service/src quotation-service/src
RUN mvn clean package -DskipTests -pl quotation-service

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/quotation-service/target/quotation-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]


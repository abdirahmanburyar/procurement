FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY config-server/pom.xml config-server/
COPY api-gateway/pom.xml api-gateway/
COPY auth-service/pom.xml auth-service/
COPY procurement-service/pom.xml procurement-service/
COPY quotation-service/pom.xml quotation-service/
COPY purchase-order-service/pom.xml purchase-order-service/
COPY inventory-service/pom.xml inventory-service/
COPY config-server/src config-server/src
RUN mvn clean package -DskipTests -pl config-server -am

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/config-server/target/config-server-1.0.0.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]


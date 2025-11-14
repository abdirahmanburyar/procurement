FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY config-server/pom.xml config-server/
COPY config-server/src config-server/src
RUN mvn clean package -DskipTests -pl config-server

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/config-server/target/config-server-1.0.0.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]


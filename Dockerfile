FROM maven:3.9.9-eclipse-temurin-21 as build

COPY src src
COPY pom.xml pom.xml

RUN mvn clean package dependency:copy-dependencies -DskipTests=true -DincludeScope=runtime

FROM bellsoft/liberica-openjdk-alpine:21

RUN adduser --system spring-boot && addgroup spring-boot && adduser spring-boot spring-boot
USER spring-boot


WORKDIR /app

COPY --from=build target/dependency ./lib
COPY --from=build target/auth-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java", "-cp", "./lib/*:./app.jar", "org.cyberchronicle.auth.AuthApplication"]

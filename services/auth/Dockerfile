FROM maven:3.9.8-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY common-lib ./common-lib
COPY services/auth/pom.xml ./services/auth/
COPY services/auth/src ./services/auth/src

RUN mvn install -N

WORKDIR /app/common-lib
RUN mvn clean install

WORKDIR /app/services/auth
RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/services/auth/target/auth-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
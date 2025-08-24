FROM maven:3.9.8-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY common-lib ./common-lib
COPY services/gateway/pom.xml ./services/gateway/
COPY services/gateway/src ./services/gateway/src

RUN mvn install -N

WORKDIR /app/common-lib
RUN mvn clean install

WORKDIR /app/services/gateway
RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/services/gateway/target/gateway-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
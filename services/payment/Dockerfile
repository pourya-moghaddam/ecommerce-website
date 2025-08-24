FROM maven:3.9.8-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY common-lib ./common-lib
COPY services/payment/pom.xml ./services/payment/
COPY services/payment/src ./services/payment/src

RUN mvn install -N

WORKDIR /app/common-lib
RUN mvn clean install

WORKDIR /app/services/payment
RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/services/payment/target/payment-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
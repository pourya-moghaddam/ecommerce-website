FROM maven:3.9.8-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY common-lib ./common-lib
COPY services/product/pom.xml ./services/product/
COPY services/product/src ./services/product/src

RUN mvn install -N

WORKDIR /app/common-lib
RUN mvn clean install

WORKDIR /app/services/product
RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/services/product/target/product-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
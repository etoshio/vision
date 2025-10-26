# ==========================
# Stage 1: build com Maven
# ==========================
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /build

COPY . /build

# Gera JAR executável do worker
RUN mvn -pl worker -am -DskipTests package spring-boot:repackage

# ==========================
# Stage 2: runtime leve
# ==========================
FROM eclipse-temurin:23-jre
WORKDIR /app

COPY --from=build /build/worker/target/*.jar app.jar

VOLUME ["/data"]

ENTRYPOINT ["java","-jar","/app/app.jar"]

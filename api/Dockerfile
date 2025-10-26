# ==========================
# Stage 1: build com Maven
# ==========================
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /build

COPY . /build

# Gera JAR executável da API (inclui o parent e dependências)
RUN mvn -pl api -am -DskipTests package spring-boot:repackage

# ==========================
# Stage 2: runtime leve
# ==========================
FROM eclipse-temurin:23-jre
WORKDIR /app

# copia o jar reempacotado da API
COPY --from=build /build/api/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

# ---- Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ---- Stage 2: Run
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# mở port (Spring Boot)
EXPOSE 8080

# chạy app
ENTRYPOINT ["java","-jar","app.jar"]
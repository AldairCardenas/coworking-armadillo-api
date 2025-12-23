# Etapa 1: Compilación
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Ajustada para encontrar el archivo JAR)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Buscamos cualquier JAR en target y lo renombramos a app.jar
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
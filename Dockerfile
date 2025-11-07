# ========================
# Fase 1: Build
# ========================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar pom.xml y descargar dependencias antes de copiar el código fuente (para cacheo eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y construir el JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ========================
# Fase 2: Runtime
# ========================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el JAR construido desde la fase anterior
COPY --from=build /app/target/*.jar app.jar

# Variables de entorno configurables
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/fruitdb
ENV SPRING_DATASOURCE_USERNAME=fruituser
ENV SPRING_DATASOURCE_PASSWORD=fruitpass
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# 🍎 S4.02 - REST API with Spring Boot and MySQL

## 📄 Description

This project consists of developing a **RESTful API** for managing a fruit store inventory, introducing a relationship between **Fruits** and their **Suppliers** using **Spring Boot** and **MySQL**.

The application implements a complete **CRUD** (Create, Read, Update, Delete) for both entities, following the **MVC architecture** and using **DTOs**, **validation**, and **exception handling** for a clean and maintainable design.

It also includes a **multi-stage Dockerfile** optimized for production and a **docker-compose** configuration for running the application together with a MySQL database.

---

## 💻 Technologies Used

- **Java 21**
- **Spring Boot 3.x**
    - Spring Web
    - Spring Data JPA
    - Spring Validation
- **MySQL 8.0**
- **Maven 3.9.x**
- **Lombok**
- **JUnit 5 + Mockito** (TDD)
- **Docker & Docker Compose**
- **Git / GitHub**

---

## 📋 Requirements

To run this project locally, make sure you have the following installed:

| Tool | Minimum Version |
|------|-----------------|
| Java | 21 |
| Maven | 3.9 |
| Docker | 24.x |
| Docker Compose | 2.x |

---

## 🛠️ Installation

1. **Clone this repository:**
   ```bash
   git clone https://github.com/alaw810/4.2-Spring-CRUD-MySQL.git
   cd 4.2-Spring-CRUD-MySQL
   ```

2. **Build the application:**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Configure environment variables (optional):**  
   The application supports configuration via environment variables:
   ```
   SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/fruitdb
   SPRING_DATASOURCE_USERNAME=fruituser
   SPRING_DATASOURCE_PASSWORD=fruitpass
   SERVER_PORT=8080
   ```

---

## ▶️ Execution

### Run with Docker Compose
This will start both the Spring Boot app and MySQL.

```bash
docker-compose up --build
```

Once started, access the API at:  
👉 [http://localhost:8080](http://localhost:8080)

---

## 🌐 Deployment

The project includes a **multi-stage Dockerfile** optimized for production:

```dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/fruitdb
ENV SPRING_DATASOURCE_USERNAME=fruituser
ENV SPRING_DATASOURCE_PASSWORD=fruitpass
ENV SERVER_PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

To build and run the image manually:
```bash
docker build -t fruit-api .
docker run -p 8080:8080 fruit-api
```

---

## 🧠 Author

**Adrià Lorente**  
📍 IT Academy – Java Back-End Development  
📚 Exercise: *S04.T02.N02 – API REST with Spring Boot (Level 2 - MySQL)*  

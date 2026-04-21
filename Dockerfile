# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Environment variables with defaults (can be overridden by docker-compose or VPS)
ENV SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/hr_management_system_db?useSSL=false&serverTimezone=Asia/Jakarta
ENV SPRING_DATASOURCE_USERNAME=xxxx
ENV SPRING_DATASOURCE_PASSWORD=yyyy

ENTRYPOINT ["java", "-jar", "app.jar"]

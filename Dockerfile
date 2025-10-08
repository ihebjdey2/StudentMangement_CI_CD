# Étape 1 : build du projet avec Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : exécution du jar
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/student-management-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]

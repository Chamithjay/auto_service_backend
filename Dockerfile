# ---- Stage 1: Build the application ----
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven Wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (caches layers)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy all source
COPY src ./src

# Build the project
RUN ./mvnw clean package -DskipTests

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from the first stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

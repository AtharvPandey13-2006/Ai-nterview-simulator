# ---- Stage 1: Build the app ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# ---- Stage 2: Run the app ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy only the final JAR from the builder stage
COPY --from=builder /app/target/interview-simulator-0.0.1-SNAPSHOT.jar app.jar

# Expose the dynamic port used by Render
EXPOSE 8080

# Render sets PORT as env var; use that
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]

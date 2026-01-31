# Stage 1: Build the JAR
FROM gradle:8.10.2-jdk21 AS builder

WORKDIR /app

# Copy Gradle files first (cache dependencies)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the app
RUN gradle bootJar --no-daemon

# Stage 2: Runtime image (small & secure)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
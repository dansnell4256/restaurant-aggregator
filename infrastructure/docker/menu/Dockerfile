FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

# Copy gradle files first for better caching
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY common common
COPY menu menu

# Build the application
RUN ./gradlew menu:bootJar

# Production stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built artifacts
COPY --from=builder /app/menu/build/libs/*.jar app.jar

# Set the startup command
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
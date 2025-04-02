FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

# Copy gradle files first for better caching
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY common common
COPY catalogue catalogue

# Build the application
RUN ./gradlew catalogue:bootJar

# Production stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built artifacts
COPY --from=builder /app/catalogue/build/libs/*.jar app.jar

# Set the startup command
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

# Copy gradle files first for better caching
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY common common
COPY orders orders

# Build the application
RUN ./gradlew orders:bootJar

# Production stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built artifacts
COPY --from=builder /app/orders/build/libs/*.jar app.jar

# Set the startup command
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
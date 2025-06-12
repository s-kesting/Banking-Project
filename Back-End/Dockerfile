FROM ubuntu:latest AS build

# Install Java 21 and Maven
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk maven

# Set working directory inside the container
WORKDIR /app

# Copy all project files into the container
COPY . .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Build the application (skipping tests for faster build)
RUN ./mvnw clean package -DskipTests

# Second stage: use a lightweight image for runtime
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
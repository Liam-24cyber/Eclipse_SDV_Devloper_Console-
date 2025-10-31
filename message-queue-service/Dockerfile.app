FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR file
COPY message-queue-service/app/target/message-queue-service-app-latest.jar app.jar

# Expose the service port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8083/api/v1/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

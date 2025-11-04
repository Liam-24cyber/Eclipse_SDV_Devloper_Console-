FROM amazoncorretto:17-al2-jdk AS build
WORKDIR /build

# Install tar and Maven 3.9.x (newer version)
RUN yum install -y tar gzip && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz | tar xz -C /opt && \
    ln -s /opt/apache-maven-3.9.5/bin/mvn /usr/local/bin/mvn

COPY webhook-management-service/pom.xml ./
COPY webhook-management-service/api/pom.xml ./api/
COPY webhook-management-service/app/pom.xml ./app/
COPY webhook-management-service/app-database/pom.xml ./app-database/
COPY webhook-management-service/api/openapi ./api/openapi/
COPY webhook-management-service/app/src ./app/src/
COPY webhook-management-service/app-database/src ./app-database/src/
RUN mvn clean install -DskipTests -Dcyclonedx.skip=true

FROM amazoncorretto:17-al2-jdk AS app
COPY --from=build /build/app/target/*.jar /app/app.jar
WORKDIR /app

# Expose the service port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8084/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM amazoncorretto:17-al2-jdk AS build
WORKDIR /build

# Install tar and Maven 3.9.x (newer version)
RUN yum install -y tar gzip && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz | tar xz -C /opt && \
    ln -s /opt/apache-maven-3.9.5/bin/mvn /usr/local/bin/mvn

COPY tracks-management-service/pom.xml tracks-management-service/settings.xml ./
COPY tracks-management-service/api/pom.xml ./api/
COPY tracks-management-service/app/pom.xml ./app/
COPY tracks-management-service/app-database/pom.xml ./app-database/
COPY tracks-management-service/api/openapi ./api/openapi/
COPY tracks-management-service/app/src ./app/src/
COPY tracks-management-service/app-database/src ./app-database/src/
RUN mvn clean install -DskipTests -Dcyclonedx.skip=true

FROM amazoncorretto:17-al2-jdk AS app
COPY --from=build /build/app/target/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]

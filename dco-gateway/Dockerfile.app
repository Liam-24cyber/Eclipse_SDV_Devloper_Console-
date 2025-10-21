FROM amazoncorretto:17-al2-jdk AS build
WORKDIR /build

# Install tar and Maven 3.9.x (newer version)
RUN yum install -y tar gzip && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz | tar xz -C /opt && \
    ln -s /opt/apache-maven-3.9.5/bin/mvn /usr/local/bin/mvn

COPY dco-gateway/pom.xml dco-gateway/settings.xml ./
COPY dco-gateway/api/pom.xml ./api/
COPY dco-gateway/app/pom.xml ./app/
COPY dco-gateway/api/openapi ./api/openapi/
COPY dco-gateway/app/src ./app/src/
RUN mvn clean install -DskipTests -Dcyclonedx.skip=true

FROM amazoncorretto:17-al2-jdk AS app
COPY --from=build /build/app/target/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]

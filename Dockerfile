# Build stage
FROM maven:3.8-openjdk-17 AS build
COPY src /home/app/src
COPY checkstyle.xml /home/app
COPY checkstyle-exclude.xml /home/app
COPY spotbugs-exclude.xml /home/app
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install -DskipITs=true -Dmaven.test.skip=true

# Package stage
FROM openjdk:21-ea-17-slim-buster
COPY --from=build /home/app/target/fleetmarkethub-*.jar /usr/local/lib/fleetmarkethub.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/fleetmarkethub.jar"]
# Stage 1: Build the application
FROM maven:3.8-openjdk-17-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file to resolve dependencies
COPY pom.xml /app/

# Copy the entire project source code, including the custom JAR in resources
COPY src /app/src

# Install the custom JAR file from resources into the local Maven repository
RUN mvn install:install-file \
    -Dfile=/app/src/main/resources/app-infrastructure-2.1.174-SNAPSHOT.jar \
    -DgroupId=com.bigid \
    -DartifactId=app-infrastructure \
    -Dversion=2.1.174-SNAPSHOT \
    -Dpackaging=jar

# Build the application, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre

# Set the working directory inside the container
WORKDIR /usr/src

# Copy the JAR file built in the previous stage
COPY --from=build /app/target/bigid-azure-key-vault-integration-app-*-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8083

# Specify the command to run the application
CMD ["java", "-jar", "/usr/src/app.jar"]

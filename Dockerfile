#Stage 1 : build the application
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Install the Apache Maven build tool
# Update package list and install Maven without recommended packages to keep the layer small
RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*

# Copy the Project Object Model (POM) file from the host to the container's WORKDIR (/app)
COPY pom.xml .

# Download project dependecies
RUN mvn dependency:go-offline -B

# Copy the application's source code
COPY src ./src

# Package the Spring Boot application into a JAR file
RUN mvn clean package -DskipTests

#Stage 2 : build a production ready image and run
# set up the runtime environtment
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the final executable JAR file from the 'builder' stage's target directory
# This is a key advantage of multi-stage builds: onlt the artifact is copied, not build tools or sources
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8090

# Define the command to run the applicaion wher the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
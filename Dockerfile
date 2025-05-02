# Use an official Maven image as a parent image
FROM maven:3.8.4-jdk-11 as build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies (only when necessary)
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src /app/src

# Package the application (this will create the JAR file)
RUN mvn clean package -DskipTests

# Use OpenJDK for the runtime environment
FROM openjdk:11-jre-slim

# Copy the JAR file from the build stage
COPY --from=build /app/target/BlogApp-0.0.1-SNAPSHOT.jar /app/BlogApp.jar

# Expose the port your application will run on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/BlogApp.jar"]

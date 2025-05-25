# This Dockerfile is used to build a Maven image with Eclipse Temurin JDK 17.
FROM maven:3.9.4-eclipse-temurin-17

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy the pom.xml file to the working directory
COPY . .

# Copy the source code to the working directory
CMD ["mvn", "compile", "exec:java", "-Dexec.mainClass=com.example.Main"]


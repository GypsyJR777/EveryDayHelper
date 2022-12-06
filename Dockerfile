FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/gypsyjr777-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
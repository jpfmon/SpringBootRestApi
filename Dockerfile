FROM amazoncorretto:17.0.8-alpine3.18
EXPOSE 8080
ARG JAR_FILE=target/challenge-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /challenge.jar
ENTRYPOINT ["java", "-jar", "challenge.jar"]
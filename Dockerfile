FROM openjdk:8-jdk-alpine
VOLUME /tmp

ARG JAR_FILE=target/simple-twitter.jar
ADD ${JAR_FILE} simple-twitter.jar

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/simple-twitter.jar"]
FROM java:8-jdk-alpine

WORKDIR /usr/app

COPY target/VKLoader-1.0-SNAPSHOT.jar .
COPY src/main/resources/application-test.properties

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "VKLoader-1.0-SNAPSHOT.jar"]
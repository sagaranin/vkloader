FROM java:8-jdk-alpine

WORKDIR /usr/app

COPY target/VKLoader-1.0-SNAPSHOT.jar ./app.jar
COPY src/main/resources/application-test.properties ./application.properties

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "-Xms4G", "-Xmx8G", "app.jar"]
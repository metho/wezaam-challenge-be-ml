FROM amazoncorretto:11-alpine-jdk
COPY target/message-server-1.0.0.jar message-server-1.0.0.jar
ENTRYPOINT ["java","-jar","/message-server-1.0.0.jar"]
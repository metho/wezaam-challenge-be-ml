FROM amazoncorretto:11-alpine-jdk
COPY target/wezaam-challenge-be-1.0.jar wezaam-challenge-be-1.0.jar
ENTRYPOINT ["java","-jar","/wezaam-challenge-be-1.0.jar"]

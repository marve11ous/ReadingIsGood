FROM openjdk:17-alpine
ARG JAR_FILE=target/reading-is-good-*.jar
COPY ${JAR_FILE} reading-is-good.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/reading-is-good.jar"]
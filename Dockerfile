FROM openjdk:17-alpine
LABEL authors="rightpair"

CMD ["./gradlew", "clean", "build"]

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
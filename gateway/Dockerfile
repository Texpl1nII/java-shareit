FROM amazoncorretto:21-alpine
COPY gateway/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
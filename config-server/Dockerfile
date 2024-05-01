FROM openjdk:21
ENV TZ=Asia/Seoul
COPY build/libs/*.jar ConfigServer.jar
COPY /src/main/resources/application.yml application.yml
COPY /src/main/resources/bootstrap.yml bootstrap.yml
CMD ["./gradlew", "clean", "build"]
ENTRYPOINT ["java","-jar","ConfigServer.jar"]
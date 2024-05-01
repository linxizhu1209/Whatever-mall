FROM openjdk:21
ARG JAR_FILE=build/libs/*.jar
ENV TZ=Asia/Seoul
COPY ${JAR_FILE} DiscoveryServer.jar
COPY /src/main/resources/application.yml application.yml
CMD ["./gradlew", "clean", "build"]
ENTRYPOINT ["java","-jar","DiscoveryServer.jar"]
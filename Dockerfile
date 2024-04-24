# Dockerfile
FROM openjdk:21
ARG JAR_FILE=build/libs/*.jar
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && \
echo $TZ > /etc/timezone && \
ln -snf /usr/share/zoneinfo/$TZ /etc/localtime
COPY ${JAR_FILE} app.jar
CMD ["./gradlew", "clean", "build"]
ENTRYPOINT ["java","-jar","app.jar"]
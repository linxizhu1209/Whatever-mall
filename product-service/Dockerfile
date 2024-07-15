FROM openjdk:21-jdk-slim
# JAR 파일 빌드 시 사용되는 변수
ARG JAR_FILE=build/libs/*.jar

# 환경 변수 설정
ENV TZ=Asia/Seoul

# 필요한 패키지 설치
RUN apt-get update && apt-get install -y iproute2

# JAR 파일 및 기타 필요한 파일 복사
COPY ${JAR_FILE} /app/ProductService.jar
COPY /src/main/resources/application.yml /app/application.yml
COPY set-ip.sh /app/set-ip.sh

# set-ip.sh 스크립트에 실행 권한 부여
RUN chmod +x /app/set-ip.sh

# 컨테이너 시작 시 set-ip.sh 스크립트를 실행하도록 설정
ENTRYPOINT ["/app/set-ip.sh"]

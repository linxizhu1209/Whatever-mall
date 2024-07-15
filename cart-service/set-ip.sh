#!/bin/bash

# IP 주소 가져오기
IP_ADDRESS=$(hostname -i)

# 환경 변수 설정
export EUREKA_INSTANCE_IP=$IP_ADDRESS

# Java 애플리케이션 실행
exec java -Djava.security.egd=file:/dev/./urandom -jar /app/CartService.jar

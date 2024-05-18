# Whatever-mall 프로젝트
일반 상품부터 한정판매 상품까지 무엇이든지 구매할 수 있는 eCommerce 플랫폼
## 💁‍♀️프로젝트 소개
마이크로서비스 아키텍처로 프로젝트를 구성하여 서비스간 독립성을 확보하고, 
재고 데이터 저장에 Redis를 활용해 한정판매 상품에 대규모 트래픽이 몰리더라도 빠른 재고 조회와 빠른 물품 구매를 지원하여, 고객의 불편을 최소화하고 만족도를 높여 고객이 원하는 것은 무엇이든지 구매할 수 있는 환경을 제공하고자 하였습니다.
## 💻주요 기능
- MSA로 구성하여 장애 발생의 범위를 최소화 
- API GATEWAY를 사용해 부하 분산 및 중요 로직 캡슐화
- Spring Cloud Config 구성하여 여러 서버의 설정파일을 중앙 관리화 
- Redis Cache를 이용해 대량의 트래픽 요청에도 빠른 응답 가능 
- Redis Redisson을 활용한 동시성 제어
-------------
## ⛓️ERD
![e-commerce](https://github.com/linxizhu1209/Whatever-mall/assets/146171215/c468a98f-3b26-441d-9706-b2f0d3202f9c)
-----------
## 📝API 문서 
[Whatever-mall API (Postman)](https://documenter.getpostman.com/view/30411399/2sA3JT1xnA)
----------
## 🔧기술 스택
- Spring Boot
- Spring Security
- JPA / Hibernate
- MySQL
- Redis
- Docker/Docker Compose

------
## 📈성능 최적화
- **MSA(MicroService Architecture) 구성** : 서비스의 확장성을 높이고, 각 서비스의 독립성을 확보하기 위해 MSA로 구성했습니다.
- **Api Gateway 활용** : API GATEWAY를 추가하여 확장성을 높이고, 서비스 관리를 용이하게 하였습니다.
- **Cache 저장소로 Redis 사용** : 재고에 대한 대규모 조회 요청 및 감소 요청을 처리하기 위해 Redis를 사용했습니다.
- -------
## 💡트러블 슈팅
- **Redis 저장 데이터 동시성 이슈 문제** : 주문으로 인한 재고감소 로직 실행 시 Redis에 저장된 재고 데이터의 동시성 이슈를 해결. [더 보기](https://blog.naver.com/dlahj1209/223441877421)
- **Feign 통신 도중 예외 발생 시 500 서버 에러 문제** : Feign 서버에서 예외 발생 시 에러 코드가 500으로 반환되는 것을 개선했습니다. [더 보기](https://blog.naver.com/dlahj1209/223447377496)
- **Docker 환경에서 yml을 읽어오지 못하는 문제** : Docker로 config서버 실행 시 yml을 읽어오지 못하는 문제를 해결했습니다. [더 보기](https://blog.naver.com/dlahj1209/223433369487)
----
## 🚀성능 테스트
Redis를 캐시 저장소로 사용함에따라, DB를 통한 재고를 조회할 때보다 성능이 어느 정도까지 향상되었는지 알아보기 위해,
Apache Jmeter를 통해 대규모 트래픽 요청 테스트를 하여 성능테스트를 진행했습니다. 

[성능 테스트 결과](https://brawny-yarrow-080.notion.site/70c8d4f15261442f9075bd151b114378?pvs=4)

---------------------
**MADE BY** [임희주](https://github.com/linxizhu1209)

**PERIOD** 2024.4.17. ~ 2024.5.19.
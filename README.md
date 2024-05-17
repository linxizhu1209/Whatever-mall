<h1>Whatever-mall 프로젝트</h1>
일반 상품부터 한정판매 상품까지 다양한 상품을 판매하는 eCommerce 플랫폼
<h2>💁‍♀️프로젝트 소개</h2>
- MSA로 구성하여 장애 발생의 범위를 최소화 <br>
- API GATEWAY를 사용해 부하 분산 및 중요 로직 캡슐화 <br>
- Cache를 이용한 조회 및 구매 작업을 통해 대량의 트래픽 요청에도 빠른 응답 가능 <br>
<h2>💻주요 기능</h2>
<h2>⛓️ERD</h2>
![e-commerce](https://github.com/linxizhu1209/Whatever-mall/assets/146171215/e1ae7ef1-86c0-47b5-aaa3-5d3e81519137)
<h2>📝API 문서</h2>
[Whatever-mall API (Postman)] (https://documenter.getpostman.com/view/30411399/2sA3JT1xnA)
<h2>🔧기술 스택</h2>
<ul>
<li>Spring Boot </li>
<li>Spring Security </li>
<li>JPA / Hibernate </li>
<li>MySQL  </li>
<li>Redis </li>
<li>Docker / Docker Compose</li>
</ul>
<h2>📈성능 최적화</h2>
- 
<h2>💡트러블 슈팅</h2>
<ul>
<li><b>Redis 저장 데이터 동시성 이슈 문제</b> : 주문으로 인한 재고감소 로직 실행 시 Redis에 저장된 재고 데이터의 동시성 이슈를 해결. [더 보기](https://blog.naver.com/dlahj1209/223441877421)</li>
<li><b>Feign 통신 도중 예외 발생 시 500 서버 에러 문제</b> : Feign 서버에서 예외 발생 시 에러 코드가 500으로 반환되는 것을 개선했습니다. [더 보기](https://blog.naver.com/dlahj1209/223447377496)</li>
<li><b>Docker 환경에서 yml을 읽어오지 못하는 문제</b> : Docker로 config서버 실행 시 yml을 읽어오지 못하는 문제를 해결했습니다. [더 보기](https://blog.naver.com/dlahj1209/223433369487)</li>
</ul>

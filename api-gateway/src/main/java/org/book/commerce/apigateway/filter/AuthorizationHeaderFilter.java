package org.book.commerce.apigateway.filter;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>{
    @Value("${jwt.secret}")
    private String secretKey;

    private RedisTemplate redisTemplate;
    @Autowired
    public AuthorizationHeaderFilter(final RedisTemplate redisTemplate){
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    @Override
    public GatewayFilter apply(AuthorizationHeaderFilter.Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestUri = request.getURI().getPath();
            String requiredRole;
            AntPathMatcher pathMatcher = new AntPathMatcher();
            if(Arrays.stream(Config.USER_URL).anyMatch(pattern-> pathMatcher.match(pattern,requestUri))){
                requiredRole = "USER";
            }
            else if(Arrays.stream(Config.ADMIN_URL).anyMatch(pattern-> pathMatcher.match(pattern,requestUri))){
                requiredRole = "ADMIN";
            }
            else return chain.filter(exchange); // 인가가 필요없는 uri면 바로 통과

            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) return onError(exchange,"No authorization header",HttpStatus.UNAUTHORIZED);
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ","");
            if(!validationToken(jwt)) return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            String userRole = resolveTokenRole(jwt).replace("[","").replace("]","");
            if(requiredRole.equalsIgnoreCase("ADMIN")){
                if(!userRole.equalsIgnoreCase("ADMIN")) return onError(exchange, "do not have permission", HttpStatus.FORBIDDEN);
            }
            else {
                if(!userRole.equalsIgnoreCase("USER")) return onError(exchange, "do not have permission", HttpStatus.FORBIDDEN);
            }
            addAuthorizationHeaders(exchange.getRequest(),getEmailByToken(jwt),jwt);
            return chain.filter(exchange);
        };
    }

    private void addAuthorizationHeaders(ServerHttpRequest request, String emailByToken,String token) {
        request.mutate().header("X-Authorization-Id",emailByToken);
        request.mutate().header("X-TOKEN",token);
    }


    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus){
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(httpStatus);
    log.error(err);
    return response.setComplete();
}

    private String resolveTokenRole(String token) {
    try {
        String subject = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("roles").toString();
        return subject;
    } catch (Exception e) {
        log.info("유저 권한 체크 실패");
        return "e";
    }
}
    public boolean validationToken(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            String isLogout = (String) redisTemplate.opsForValue().get(token);
            if (!ObjectUtils.isEmpty(isLogout))
                throw new IllegalAccessException();  // 이미 로그아웃된 상태라면 isLogout이 있을 것이므로 없는 경우에만 true반환
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException mfj) {
            log.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException eje) {
            log.debug("만료된 JWT 토큰입니다");
        } catch (IllegalAccessException iae) {
            log.debug("이미 로그아웃된 유저입니다");
        }
        return false;
    }

    public String getEmailByToken(String token){
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.isEmpty()?null:claims.get("sub",String.class);
    }

    public static class Config {

        @Bean
        public static PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }

        public static final String[] PERMIT_URL = {
                "/","/**","/auth/**","/css/**","/images/**","/js/**","/favicon.ico","/h2-console/**"
                ,"/swagger-ui.html","/swagger-ui/**","/api-docs/**","/v3/**","/auth/login/**","/product/**"
                ,"/eureka"
        };
        public static final String[] USER_URL = {
                 "/auth/logout","/cart/**","/order/**","/wish/**","/user/**"
        };
        public static final String[] ADMIN_URL = {
                "/admin/**", "/product/admin/**"
        };

}
}


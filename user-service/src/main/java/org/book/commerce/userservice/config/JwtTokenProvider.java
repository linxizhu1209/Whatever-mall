package org.book.commerce.userservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.userservice.domain.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final String AUTHORITIES_KEY = "roles";
    private final Long ACCESS_TOKEN_EXPIRED_TIME = 60*60*1000L;
    private final Long REFRESH_TOKEN_EXPIRED_TIME = 7*24*60*60*1000L;


    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String email,String role, Long expireLength){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put(AUTHORITIES_KEY,role);
        Date now = new Date();
        Date validity = new Date(now.getTime()+expireLength);
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256,secretKey).compact();
    }


    public String createAccessToken(Users user){
        return createToken(user.getEmail(),user.getRole().name(),ACCESS_TOKEN_EXPIRED_TIME);
    }

    public String createRefreshToken(Users user){
        return createToken(user.getEmail(),user.getRole().name(),REFRESH_TOKEN_EXPIRED_TIME);
    }

    public String getEmailByToken(String token){
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.isEmpty()?null:claims.get("sub",String.class);
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken)
                .getBody().getExpiration();
        return expiration.getTime()-new Date().getTime(); 

    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

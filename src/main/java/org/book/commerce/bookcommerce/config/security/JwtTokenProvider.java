package org.book.commerce.bookcommerce.config.security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.bookcommerce.repository.entity.Users;
import org.book.commerce.bookcommerce.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final String AUTHORITIES_KEY = "roles"; // todo 추후 수정
    private final Long ACCESS_TOKEN_EXPIRED_TIME = 60*60*1000L;
    private final Long REFRESH_TOKEN_EXPIRED_TIME = 7*24*60*60*1000L;
    private final RedisTemplate redisTemplate;
    private final CustomUserDetailService customUserDetailService;

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

    private String getUserEmail(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }  // todo 수정예정 (deprecated)

    public Authentication getAuthentication(String token){
        UserDetails userDetails = customUserDetailService.loadUserByUsername(this.parseClaims(token));
        return new UsernamePasswordAuthenticationToken(userDetails,token,userDetails.getAuthorities());
    }

    public String createAccessToken(Users user){
        return createToken(user.getEmail(),user.getRole().name(),ACCESS_TOKEN_EXPIRED_TIME);
    }

    public String createRefreshToken(Users user){
        return createToken(user.getEmail(),user.getRole().name(),REFRESH_TOKEN_EXPIRED_TIME);
    }

    public boolean validationToken(String token){
        try{
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            String isLogout = (String)redisTemplate.opsForValue().get(token);
            if(!ObjectUtils.isEmpty(isLogout)) throw new IllegalAccessException();  // 이미 로그아웃된 상태라면 isLogout이 있을 것이므로 없는 경우에만 true반환
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException mfj){
            log.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException eje){
            log.debug("만료된 JWT 토큰입니다");
        } catch (IllegalAccessException iae){
            log.debug("이미 로그아웃된 유저입니다");
        }
        return false;
    }

    private String parseClaims(String accessToken){
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody().getSubject();
    }

    public String getEmailByToken(String token){
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.isEmpty()?null:claims.get("sub",String.class);
    }

    public String resolveAccessToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        log.info(token);
        if(StringUtils.hasText(token)){
            return token;
        }
        return null;
    }


    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken)
                .getBody().getExpiration();
        return expiration.getTime()-new Date().getTime(); // 만료시간에서 현재 시간을 뺀 만큼 로그아웃된 토큰을 블랙리스트해줄거임

    }
}

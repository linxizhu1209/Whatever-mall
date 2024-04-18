package org.book.commerce.bookcommerce.config.security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.bookcommerce.repository.UsersRepository;
import org.book.commerce.bookcommerce.repository.entity.Users;
import org.book.commerce.bookcommerce.service.exception.AuthService;
import org.book.commerce.bookcommerce.service.exception.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;

    private final String AUTHORITIES_KEY = "roles"; // todo 추후 수정
    private final Long ACCESS_TOKEN_EXPIRED_TIME = 60*60*1000L;
    private final Long REFRESH_TOKEN_EXPIRED_TIME = 7*24*60*60*1000L;
    private final UsersRepository usersRepository;
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
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,token,userDetails.getAuthorities());
        return authentication;
    }

    public String createAccessToken(String email){
            Users user = usersRepository.findByEmail(email).orElseThrow();
            return createToken(email,user.getRole().name(),ACCESS_TOKEN_EXPIRED_TIME);
    }

    public String createRefreshToken(String email){
        Users user = usersRepository.findByEmail(email).orElseThrow();
        return createToken(email,user.getRole().name(),REFRESH_TOKEN_EXPIRED_TIME);
    }

    public boolean validationToken(String token){
        try{
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException mfj){
            log.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException eje){
            log.debug("만료된 JWT 토큰입니다");
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

}

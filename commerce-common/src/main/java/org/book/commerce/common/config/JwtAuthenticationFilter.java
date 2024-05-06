package org.book.commerce.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final String PREFIX = "Bearer";
    private final String AUTHORIZATION_HEADER = "Authorization";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        String jwt = resolveToken(request);

        if(StringUtils.hasText(jwt)&& jwtTokenProvider.validationToken(jwt)){
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다,uri: {}",authentication.getName(),requestURI);
        }
        else{
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}",requestURI);
        }
        filterChain.doFilter(request,response);
    }


    private String resolveToken(HttpServletRequest request){
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(token) && token.startsWith(PREFIX)) {
            return token.substring(7);    // "Bearer "를 뺀 값, 즉 토큰 값
        }
        return null;
    }
}
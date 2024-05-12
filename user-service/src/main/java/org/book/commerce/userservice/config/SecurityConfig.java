package org.book.commerce.userservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//    private final JwtTokenProvider jwtTokenProvider;
//
//    private final String[] PERMIT_URL = {
//            "/","/**","/auth/**","/css/**","/images/**","/js/**","/favicon.ico","/h2-console/**"
//            ,"/swagger-ui.html","/swagger-ui/**","/api-docs/**","/v3/**","/auth/login/**","/product/**"
//            ,"/eureka"
//    };
//    private final String[] USER_URL = {
//            "/user/**", "auth/logout","/cart/**","/order/**"
//    };
//    private final String[] ADMIN_URL = {
//            "/admin/**", "/product/admin/**"
//    };
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf)->csrf.disable())
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin((auth)->auth.disable()).rememberMe((remember)->remember.disable())
                .authorizeHttpRequests((auth)->auth.anyRequest().permitAll());
        log.info("security filter실행");
        return http.build();
    }}
//
//    @Bean
//    public static PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//}
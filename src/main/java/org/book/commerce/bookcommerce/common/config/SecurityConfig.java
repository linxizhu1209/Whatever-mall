package org.book.commerce.bookcommerce.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.bookcommerce.common.security.JwtAuthenticationFilter;
import org.book.commerce.bookcommerce.common.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final String[] PERMIT_URL = {
            "/","/**","/auth/**","/css/**","/images/**","/js/**","/favicon.ico","/h2-console/**"
            ,"/swagger-ui.html","/swagger-ui/**","/api-docs/**","/v3/**","/auth/login/**","/product/**"
    };
    private final String[] USER_URL = {
            "/user/**", "auth/logout",
    };
    private final String[] ADMIN_URL = {
            "/admin/**", "/product/admin/**"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf)->csrf.disable())
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin((auth)->auth.disable()).rememberMe((remember)->remember.disable())
                .authorizeHttpRequests((auth)->auth.requestMatchers(PERMIT_URL).permitAll()
                        .requestMatchers(USER_URL).hasRole("USER")
                        .requestMatchers(ADMIN_URL).hasRole("ADMIN"))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        log.info("security filter실행");
        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
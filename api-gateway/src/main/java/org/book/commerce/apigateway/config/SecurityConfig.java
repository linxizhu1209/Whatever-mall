package org.book.commerce.apigateway.config;

import org.book.commerce.apigateway.filter.AuthorizationHeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig {
//    private final String[] PERMIT_URL = {
//            "/", "/**", "/auth/**", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**"
//            , "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/**", "/auth/login/**", "/product/**"
//            , "/eureka"
//    };
//    private final String[] USER_URL = {
//            "/user/**", "auth/logout", "/cart/**", "/order/**"
//    };
//    private final String[] ADMIN_URL = {
//            "/admin/**", "/product/admin/**"
//    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .securityContextRepository(new StatelessWebSessionSecurityContextRepository())
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .anyExchange().permitAll()).build();
    }


    private static class StatelessWebSessionSecurityContextRepository implements ServerSecurityContextRepository {

        private static final Mono<SecurityContext> EMPTY_CONTEXT = Mono.empty();


        @Override
        public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
            return Mono.empty();
        }

        @Override
        public Mono<SecurityContext> load(ServerWebExchange exchange) {
            return EMPTY_CONTEXT;
        }
    }
}
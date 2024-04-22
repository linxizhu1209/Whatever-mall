package org.book.commerce.bookcommerce.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        Info info = new Info().title("Ecommerce 서비스 API 명세")
                .description("Ecommerce 서비스 이용 명세서입니다")
                .contact(new Contact().email("dlahj1209@naver.com").name("LIMHEEJU")
                        .url("https://blog.naver.com/dlahj1209"));

        String jwtSchemaName = "jwtAuth";
        /**
         * 1. type -> http 스키마를 사용하여, http 요청 헤더를 통해 인증을 수행함
         * 2. http 요청의 인증스키마 설정, bearer토큰을 사용하여 인증을 수행하도록 설정
         * 3. bearerFormat => bearer토큰의 형식으르 설정.
         * 4. in => 인증정보를 어디에 넣을지 설정.
         */
        SecurityScheme bearerAuth = new SecurityScheme().name(jwtSchemaName)
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);


        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList("Authorization"); // Authorization 헤더에 보안 스키마 추가

        return new OpenAPI().components(new Components().addSecuritySchemes("Authorization",bearerAuth))
                .addSecurityItem(addSecurityItem).info(info);
    }
}

package org.book.commerce.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"org.book.commerce.cartservice", "org.book.commerce.userservice","org.book.commerce.productservice","org.book.commerce.common"})
@EnableJpaAuditing
@EnableScheduling
public class CartServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(org.book.commerce.cartservice.CartServiceApplication.class, args);
	}

}

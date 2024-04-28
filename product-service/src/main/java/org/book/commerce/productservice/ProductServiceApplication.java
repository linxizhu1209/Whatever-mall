package org.book.commerce.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"org.book.commerce.productservice","org.book.commerce.common"})
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableScheduling
public class ProductServiceApplication {

	public static void main(String[] args) {
//		System.setProperty("spring.config.name","application-product.yml,application.yml");
		SpringApplication.run(org.book.commerce.productservice.ProductServiceApplication.class, args);
	}

}

package org.book.commerce.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"org.book.commerce.userservice","org.book.commerce.productservice","org.book.commerce.common"})
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableScheduling
public class UserServiceApplication {

	public static void main(String[] args) {
//		System.setProperty("spring.config.name","application-user,application-common");
		SpringApplication.run(UserServiceApplication.class, args);
	}

}

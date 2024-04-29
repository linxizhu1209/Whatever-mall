package org.book.commerce.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"org.book.commerce.orderservice","org.book.commerce.common"})
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class OrderServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(org.book.commerce.orderservice.OrderServiceApplication.class, args);
	}

}

package com.ofss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TransactionMsjdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionMsjdbcApplication.class, args);
	}

	@Bean
	@LoadBalanced // required for inter microservices communication
	public RestTemplate giveMeRestTemplateObject() {
		System.out.println("Returning RestTemplate object.....");
		return new RestTemplate();
	}
}

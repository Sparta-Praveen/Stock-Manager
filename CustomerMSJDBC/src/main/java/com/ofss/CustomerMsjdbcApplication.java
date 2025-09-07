package com.ofss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CustomerMsjdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerMsjdbcApplication.class, args);
	}

	@Bean
	@LoadBalanced
	@Primary
	public RestTemplate createRestTemplate() {
		return new RestTemplate();
	}
}

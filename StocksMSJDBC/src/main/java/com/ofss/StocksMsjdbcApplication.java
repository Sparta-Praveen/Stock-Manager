package com.ofss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StocksMsjdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(StocksMsjdbcApplication.class, args);
	}

}

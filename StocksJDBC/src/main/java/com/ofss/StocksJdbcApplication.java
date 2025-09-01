package com.ofss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StocksJdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(StocksJdbcApplication.class, args);
		System.out.println("Database connected Successfully!");
	}

}

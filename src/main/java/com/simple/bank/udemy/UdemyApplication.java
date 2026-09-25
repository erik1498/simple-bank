package com.simple.bank.udemy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UdemyApplication {
	public static void main(String[] args) {
		SpringApplication.run(UdemyApplication.class, args);
	}
}

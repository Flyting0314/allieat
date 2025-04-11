package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  //使用排程器一定要在application加這個
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

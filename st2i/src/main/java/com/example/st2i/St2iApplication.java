package com.example.st2i;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class St2iApplication {

	public static void main(String[] args) {
		SpringApplication.run(St2iApplication.class, args);
	}

}

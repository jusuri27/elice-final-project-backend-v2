package com.hr_handlers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HrHandlersApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrHandlersApplication.class, args);
	}

}

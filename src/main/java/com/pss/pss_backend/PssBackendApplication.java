package com.pss.pss_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PssBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PssBackendApplication.class, args);
	}

}

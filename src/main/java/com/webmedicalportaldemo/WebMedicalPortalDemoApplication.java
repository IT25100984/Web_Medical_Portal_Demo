package com.webmedicalportaldemo;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class WebMedicalPortalDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebMedicalPortalDemoApplication.class, args);
	}

}

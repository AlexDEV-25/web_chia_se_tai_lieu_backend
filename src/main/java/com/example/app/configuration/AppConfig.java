package com.example.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

	@Bean
	RestTemplate restTemplate() {
		// dùng để gọi api từ spring
		// đang dùng trong fileManager
		return new RestTemplate();
	}
}

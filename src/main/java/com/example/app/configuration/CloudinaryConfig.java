package com.example.app.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration

public class CloudinaryConfig {

	@Value("${app.cloud.name}")
	private String cloudName;

	@Value("${app.cloud.key}")
	private String cloudKey;

	@Value("${app.cloud.secret}")
	private String cloudSecret;

	@Bean
	Cloudinary cloudinary() {
		Cloudinary cloudinary = new Cloudinary(//
				ObjectUtils.asMap(//
						"cloud_name", cloudName, //
						"api_key", cloudKey, //
						"api_secret", cloudSecret, //
						"secure", true));
		return cloudinary;
	}

}

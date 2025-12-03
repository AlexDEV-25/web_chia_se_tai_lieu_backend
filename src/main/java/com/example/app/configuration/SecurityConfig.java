package com.example.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final String[] PUBLIC_ENDPOINTS_POST = { "/api/users/auth/register", "/api/auth/log-in",
			"/api/auth/log-out", "/api/auth/introspect" };

	private final String[] PUBLIC_ENDPOINTS_GET = { "/api/categories", "/api/categories/{id}",
			"/api/comments/document/{docId}", "/api/documents", "/api/documents/{id}", "/api/documents/user/{userId}",
			"/api/documents/category/{categoryId}", "/api/documents/view/{id}", "/api/ratings/document/{docId}" };

	private CustomJwtDecoder customJwtDecoder;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests(request -> //
		request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS_POST).permitAll()//
				.requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS_GET).permitAll()//
				.anyRequest().authenticated());

		httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
				.jwtAuthenticationConverter(jwtAuthenticationConverter())));
		httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
		return httpSecurity.build();
	}

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

		return jwtAuthenticationConverter;
	}

}
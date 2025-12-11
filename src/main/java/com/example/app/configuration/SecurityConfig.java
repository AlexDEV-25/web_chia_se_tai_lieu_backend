package com.example.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final String[] PUBLIC_ENDPOINTS_POST = { "/api/auth/register", "/api/auth/log-in", "/api/auth/log-out",
			"/api/auth/introspect", "/api/auth/refresh-token" };

	private final String[] PUBLIC_ENDPOINTS_GET = { "/api/categories", "/api/comments/document/{docId}",
			"/api/documents", "/api/documents/{id}", "/api/documents/user/{userId}",
			"/api/documents/category/{categoryId}", "/api/documents/view/{id}", "/api/documents/{id}/file",
			"/api/ratings/document/{docId}", "/api/users/email/{email:.+}", "/api/users/username/{username}",
			"/api/images/**", "/api/auth/activate" };

	private CustomJwtDecoder customJwtDecoder;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors(Customizer.withDefaults());// <-- BẮT BUỘC để CORS hoạt động với Security
		httpSecurity.authorizeHttpRequests(request -> //
		request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS_POST).permitAll()//
				.requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS_GET).permitAll()//
				.anyRequest().authenticated());

		httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
				.jwtAuthenticationConverter(jwtAuthenticationConverter())));

		httpSecurity.exceptionHandling(ex -> ex.authenticationEntryPoint(new CustomAuthEntryPoint()));

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

	@Bean
	CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();

		corsConfiguration.addAllowedOrigin("http://localhost:5173");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}
}
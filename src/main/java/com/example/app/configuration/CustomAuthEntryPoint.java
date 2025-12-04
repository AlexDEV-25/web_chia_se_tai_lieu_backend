package com.example.app.configuration;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.app.dto.response.APIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		response.setStatus(401);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		APIResponse<String> apiResponse = new APIResponse<String>();
		apiResponse.setCode(1111);
		apiResponse.setMessage("UNAUTHENTICATED");

		ObjectMapper objectMapper = new ObjectMapper();

		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
		response.flushBuffer();
	}
}

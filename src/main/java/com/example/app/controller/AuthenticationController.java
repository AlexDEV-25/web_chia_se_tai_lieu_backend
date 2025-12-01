package com.example.app.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.AuthenticationRequest;
import com.example.app.dto.request.IntrospectRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.AuthenticationResponse;
import com.example.app.dto.response.IntrospectResponse;
import com.example.app.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@PostMapping("/log-in")
	APIResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest dto) {
		APIResponse<AuthenticationResponse> apiResponse = new APIResponse<AuthenticationResponse>();
		AuthenticationResponse resutl = authenticationService.authenticated(dto);
		apiResponse.setResult(resutl);
		if (resutl.isAuthenticated()) {
			apiResponse.setMessage("login success");
		} else {
			apiResponse.setMessage("login false");
		}
		return apiResponse;
	}

	@PostMapping("/introspect")
	APIResponse<IntrospectResponse> login(@RequestBody IntrospectRequest dto) {
		APIResponse<IntrospectResponse> apiResponse = new APIResponse<IntrospectResponse>();
		IntrospectResponse resutl;
		try {
			resutl = authenticationService.introspect(dto);
			apiResponse.setResult(resutl);
			if (resutl.isValid()) {
				apiResponse.setMessage("token right");
			} else {
				apiResponse.setMessage("token wrong");
			}
		} catch (JOSEException e) {
			throw new RuntimeException("JOSEException");
		} catch (ParseException e) {
			throw new RuntimeException("ParseException");
		}
		return apiResponse;
	}
}

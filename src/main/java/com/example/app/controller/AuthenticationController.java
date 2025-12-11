package com.example.app.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.AuthenticationRequest;
import com.example.app.dto.request.TokenRequest;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.AuthenticationResponse;
import com.example.app.dto.response.IntrospectResponse;
import com.example.app.dto.response.UserResponse;
import com.example.app.exception.AppException;
import com.example.app.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@PostMapping("/log-in")
	APIResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest dto) {
		APIResponse<AuthenticationResponse> apiResponse = new APIResponse<AuthenticationResponse>();
		AuthenticationResponse resutl = authenticationService.login(dto);
		apiResponse.setResult(resutl);
		if (resutl.isAuthenticated()) {
			apiResponse.setMessage("login success");
		} else {
			apiResponse.setMessage("login false");
		}
		return apiResponse;
	}

	@PostMapping("/log-out")
	APIResponse<Void> logout(@RequestBody TokenRequest dto) throws JOSEException, ParseException, AppException {
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		authenticationService.logout(dto);
		apiResponse.setMessage("logout success");
		return apiResponse;
	}

	@PostMapping("/register")
	public APIResponse<UserResponse> register(@RequestBody UserRequest dto) {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		UserResponse response = authenticationService.register(dto);
		apiResponse.setResult(response);
		apiResponse.setMessage("register success");
		return apiResponse;
	}

	@GetMapping("/activate")
	public APIResponse<Void> activateAccount(@RequestParam String email, @RequestParam String activationCode) {
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		authenticationService.activateAccount(email, activationCode);
		apiResponse.setMessage("acctivate success");
		return apiResponse;
	}

	@PostMapping("/refresh-token")
	APIResponse<AuthenticationResponse> refreshToken(@RequestBody TokenRequest dto)
			throws JOSEException, ParseException, AppException {
		APIResponse<AuthenticationResponse> apiResponse = new APIResponse<AuthenticationResponse>();
		AuthenticationResponse resutl = authenticationService.refreshToken(dto);
		apiResponse.setResult(resutl);
		if (resutl.isAuthenticated()) {
			apiResponse.setMessage("refresh success");
		} else {
			apiResponse.setMessage("refresh false");
		}
		return apiResponse;
	}

	@PostMapping("/introspect")
	APIResponse<IntrospectResponse> introspect(@RequestBody TokenRequest dto) throws JOSEException, ParseException {
		APIResponse<IntrospectResponse> apiResponse = new APIResponse<IntrospectResponse>();
		IntrospectResponse resutl;
		resutl = authenticationService.introspect(dto);
		apiResponse.setResult(resutl);
		if (resutl.isValid()) {
			apiResponse.setMessage("token right");
		} else {
			apiResponse.setMessage("token wrong");
		}
		return apiResponse;
	}
}

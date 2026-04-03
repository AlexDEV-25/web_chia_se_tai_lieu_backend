package com.example.app.controller;

import java.text.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.ActiveAccountRequest;
import com.example.app.dto.request.AuthenticationRequest;
import com.example.app.dto.request.ForgotPasswordRequest;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.authentication.AuthenticationResponse;
import com.example.app.dto.response.authentication.IntrospectResponse;
import com.example.app.dto.response.user.UserResponse;
import com.example.app.exception.AppException;
import com.example.app.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import jakarta.servlet.http.HttpServletRequest;
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

	@PostMapping("/log-in-google")
	APIResponse<AuthenticationResponse> loginWithGoogle(@RequestParam String code) {
		APIResponse<AuthenticationResponse> apiResponse = new APIResponse<AuthenticationResponse>();

		AuthenticationResponse resutl = authenticationService.loginWithGoogle(code);
		apiResponse.setResult(resutl);
		if (resutl.isAuthenticated()) {
			apiResponse.setMessage("login success");
		} else {
			apiResponse.setMessage("login false");
		}
		return apiResponse;
	}

	@PostMapping("/register")
	public APIResponse<UserResponse> register(@RequestBody @Valid UserRequest dto) {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		UserResponse response = authenticationService.register(dto);
		apiResponse.setResult(response);
		apiResponse.setMessage("register success");
		return apiResponse;
	}

	@PostMapping("/activate")
	public APIResponse<Void> activateAccount(@RequestBody ActiveAccountRequest dto) {
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		authenticationService.activateAccount(dto);
		apiResponse.setMessage("acctivate success");
		return apiResponse;
	}

	@PostMapping("/forgot-password")
	public APIResponse<Void> forgotPassword(@RequestParam String email) {
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		authenticationService.forgotPassword(email);
		apiResponse.setMessage("send email success");
		return apiResponse;
	}

	@PostMapping("/change-password")
	public APIResponse<UserResponse> ChangePassword(@RequestBody ForgotPasswordRequest dto) {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		authenticationService.changePassword(dto);
		apiResponse.setMessage("change password success");
		return apiResponse;
	}

	@PostMapping("/refresh-token")
	APIResponse<AuthenticationResponse> refreshToken(HttpServletRequest dto)
			throws JOSEException, ParseException, AppException {
		String oldToken = this.extractTokenFromHeader(dto);
		APIResponse<AuthenticationResponse> apiResponse = new APIResponse<AuthenticationResponse>();
		AuthenticationResponse resutl = authenticationService.refreshToken(oldToken);
		apiResponse.setResult(resutl);
		if (resutl.isAuthenticated()) {
			apiResponse.setMessage("refresh success");
		} else {
			apiResponse.setMessage("refresh false");
		}
		return apiResponse;
	}

	@PostMapping("/introspect")
	APIResponse<IntrospectResponse> introspect(HttpServletRequest dto) throws JOSEException, ParseException {
		String token = this.extractTokenFromHeader(dto);
		APIResponse<IntrospectResponse> apiResponse = new APIResponse<IntrospectResponse>();
		IntrospectResponse resutl;
		resutl = authenticationService.introspect(token);
		apiResponse.setResult(resutl);
		if (resutl.isValid()) {
			apiResponse.setMessage("token right");
		} else {
			apiResponse.setMessage("token wrong");
		}
		return apiResponse;
	}

	private String extractTokenFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new AppException("thiếu token", 1001, HttpStatus.BAD_REQUEST);
		}

		return authHeader.substring(7);
	}
}

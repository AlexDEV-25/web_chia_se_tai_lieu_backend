package com.example.app.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.constant.AppError;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@PostMapping("/log-in")
	APIResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest dto) {
		APIResponse<AuthenticationResponse> apiResponse = new APIResponse<AuthenticationResponse>();
		apiResponse.setResult(authenticationService.login(dto));
		return apiResponse;
	}

	@PostMapping("/log-in-google")
	APIResponse<AuthenticationResponse> loginWithGoogle(@RequestParam String code) {
		APIResponse<AuthenticationResponse> apiResponse = new APIResponse<AuthenticationResponse>();
		apiResponse.setResult(authenticationService.loginWithGoogle(code));
		return apiResponse;
	}

	@PostMapping("/register")
	public APIResponse<UserResponse> register(@RequestBody @Valid UserRequest dto) {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResult(authenticationService.register(dto));
		return apiResponse;
	}

	@PostMapping("/activate")
	public APIResponse<Void> activateAccount(@RequestBody ActiveAccountRequest dto) {
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		authenticationService.activateAccount(dto);
		return apiResponse;
	}

	@PostMapping("/forgot-password")
	public APIResponse<Void> forgotPassword(@RequestParam String email) {
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		authenticationService.forgotPassword(email);
		return apiResponse;
	}

	@PostMapping("/change-password")
	public APIResponse<UserResponse> ChangePassword(@RequestBody ForgotPasswordRequest dto) {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResult(authenticationService.changePassword(dto));
		return apiResponse;
	}

	@PostMapping("/refresh-token")
	APIResponse<AuthenticationResponse> refreshToken(HttpServletRequest dto) {
		String oldToken = this.extractTokenFromHeader(dto);
		APIResponse<AuthenticationResponse> apiResponse = new APIResponse<AuthenticationResponse>();
		apiResponse.setResult(authenticationService.refreshToken(oldToken));
		return apiResponse;
	}

	@PostMapping("/introspect")
	APIResponse<IntrospectResponse> introspect(HttpServletRequest dto) {
		String token = this.extractTokenFromHeader(dto);
		APIResponse<IntrospectResponse> apiResponse = new APIResponse<IntrospectResponse>();
		apiResponse.setResult(authenticationService.introspect(token));
		return apiResponse;
	}

	private String extractTokenFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw AppException.builder().appError(AppError.MISSING_TOKEN).build();
		}

		return authHeader.substring(7).trim();
	}
}

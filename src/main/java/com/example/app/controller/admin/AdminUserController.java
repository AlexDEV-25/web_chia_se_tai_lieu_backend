package com.example.app.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.DisplayRequest;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.user.UserResponse;
import com.example.app.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users/admin")
@AllArgsConstructor
public class AdminUserController {
	private final UserService userService;

	@PutMapping("hide/{id}")
	public APIResponse<UserResponse> hide(@PathVariable Long id, @RequestBody @Valid DisplayRequest dto) {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResult(userService.hide(id, dto));
		return apiResponse;
	}

	@GetMapping
	public APIResponse<UserResponse> getAll() {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResultList(userService.getAllUsers());
		return apiResponse;
	}

	@PostMapping
	public APIResponse<UserResponse> create(@RequestBody @Valid UserRequest dto) {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResult(userService.save(dto));
		return apiResponse;
	}

}

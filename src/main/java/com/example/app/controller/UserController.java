package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.UserResponse;
import com.example.app.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/{id}")
	public APIResponse<UserResponse> getById(@PathVariable Long id) {
		UserResponse response = userService.findById(id);
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<UserResponse> getAll() {
		List<UserResponse> response = userService.getAllUsers();
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<UserResponse> delete(@PathVariable Long id) {
		userService.delete(id);
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@PostMapping
	public APIResponse<UserResponse> create(@RequestBody UserRequest dto) {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		if (!this.checkEmailExist(dto.getEmail())) {
			UserResponse response = userService.save(dto);
			apiResponse.setResult(response);
			apiResponse.setMessage("save success");
		} else {
			apiResponse.setMessage("email exist");
		}
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<UserResponse> update(@PathVariable Long id, @RequestBody UserRequest dto) {
		UserResponse response = userService.update(id, dto);
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@GetMapping("/email/{email:.+}")
	public boolean checkEmailExist(@PathVariable String email) {
		return userService.checkEmailExist(email);
	}
}

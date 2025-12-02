package com.example.app.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.UserResponse;
import com.example.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	@GetMapping("/my-info")
	public APIResponse<UserResponse> getMyInfo() {
		UserResponse response = userService.getMyInfo();
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get my info success");
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
		UserResponse response = userService.save(dto);
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public APIResponse<UserResponse> update(@PathVariable Long id, @RequestPart("avt") MultipartFile avt,
			@RequestPart("data") String dataJson) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			UserRequest dto = mapper.readValue(dataJson, UserRequest.class);

			UserResponse response = userService.update(id, avt, dto);
			APIResponse<UserResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thành công");
			apiResponse.setResult(response);
			return apiResponse;

		} catch (Exception e) {
			APIResponse<UserResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thất bại: " + e.getMessage());
			return apiResponse;
		}
	}
}

package com.example.app.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.constant.AppError;
import com.example.app.dto.request.ChangePasswordRequest;
import com.example.app.dto.request.ChangeUserInfoRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.user.UserBioResponse;
import com.example.app.dto.response.user.UserResponse;
import com.example.app.exception.AppException;
import com.example.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/info/{id}")
	public APIResponse<UserBioResponse> getInfo(@PathVariable Long id) {
		APIResponse<UserBioResponse> apiResponse = new APIResponse<UserBioResponse>();
		apiResponse.setResult(userService.getUserInfo(id));
		return apiResponse;
	}

	@GetMapping("/my-info")
	public APIResponse<UserResponse> getMyInfo() {
		APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
		apiResponse.setResult(userService.getMyInfo());
		return apiResponse;
	}

	@PutMapping(value = "/my-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public APIResponse<UserResponse> updateMyInfo(@RequestPart(value = "avt", required = false) MultipartFile avt,
			@RequestPart("data") String dataJson) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ChangeUserInfoRequest dto = mapper.readValue(dataJson, ChangeUserInfoRequest.class);

			APIResponse<UserResponse> apiResponse = new APIResponse<UserResponse>();
			apiResponse.setResult(userService.updateMyinfo(avt, dto));
			return apiResponse;

		} catch (Exception e) {
			throw AppException.builder().appError(AppError.INVALID_JSON_FORMAT).build();
		}
	}

	@PutMapping("/change-password")
	public APIResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		userService.changePassword(request);
		return apiResponse;
	}

	@GetMapping("/search")
	public APIResponse<UserBioResponse> search(@RequestParam String keyword) {
		APIResponse<UserBioResponse> apiResponse = new APIResponse<UserBioResponse>();
		apiResponse.setResultList(userService.search(keyword));
		return apiResponse;
	}

	@GetMapping("/email/{email:.+}")
	public APIResponse<Boolean> checkEmailExist(@PathVariable String email) {
		APIResponse<Boolean> apiResponse = new APIResponse<Boolean>();
		apiResponse.setResult(userService.checkEmailExists(email));
		return apiResponse;
	}

	@GetMapping("/username/{username}")
	public APIResponse<Boolean> checkUsernameExists(@PathVariable String username) {
		APIResponse<Boolean> apiResponse = new APIResponse<Boolean>();
		apiResponse.setResult(userService.checkUsernameExists(username));
		return apiResponse;
	}

}

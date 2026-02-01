package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.UserFollowRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.UserFollowResponse;
import com.example.app.service.UserFollowService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/follows")
@AllArgsConstructor
@Slf4j
public class UserFollowController {
	private final UserFollowService userFollowService;

	@PostMapping
	public APIResponse<UserFollowResponse> save(@RequestBody @Valid UserFollowRequest dto) {
		UserFollowResponse response = userFollowService.save(dto);
		APIResponse<UserFollowResponse> apiResponse = new APIResponse<UserFollowResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<Void> delete(@PathVariable Long id) {
		userFollowService.delete(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("unfollow success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<UserFollowResponse> getAll() {
		List<UserFollowResponse> response = userFollowService.getFollowingByFollower();
		APIResponse<UserFollowResponse> apiResponse = new APIResponse<UserFollowResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

}

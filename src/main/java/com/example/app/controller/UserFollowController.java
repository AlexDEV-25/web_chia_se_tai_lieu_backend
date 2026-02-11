package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.FollowCountResponse;
import com.example.app.dto.response.UserFollowResponse;
import com.example.app.service.UserFollowService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/follows")
@AllArgsConstructor
@Slf4j
public class UserFollowController {
	private final UserFollowService userFollowService;

	@PostMapping("/{followingId}")
	public APIResponse<UserFollowResponse> save(@PathVariable Long followingId) {
		UserFollowResponse response = userFollowService.save(followingId);
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

	@GetMapping("/following")
	public APIResponse<UserFollowResponse> getFollowing() {
		List<UserFollowResponse> response = userFollowService.getFollowingByFollower();
		APIResponse<UserFollowResponse> apiResponse = new APIResponse<UserFollowResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/follower")
	public APIResponse<UserFollowResponse> getFollower() {
		List<UserFollowResponse> response = userFollowService.getFollowerByFollowing();
		APIResponse<UserFollowResponse> apiResponse = new APIResponse<UserFollowResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/my-follow-count")
	public APIResponse<FollowCountResponse> getMyFollowCount() {
		FollowCountResponse response = userFollowService.getMyFollowCount();
		APIResponse<FollowCountResponse> apiResponse = new APIResponse<FollowCountResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/follow-count/{userId}")
	public APIResponse<FollowCountResponse> getFollowCount(@PathVariable Long userId) {
		FollowCountResponse response = userFollowService.getFollowCount(userId);
		APIResponse<FollowCountResponse> apiResponse = new APIResponse<FollowCountResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

}

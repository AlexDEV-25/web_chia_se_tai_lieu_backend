package com.example.app.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.userfollow.FollowCountResponse;
import com.example.app.dto.response.userfollow.UserFollowResponse;
import com.example.app.service.UserFollowService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/follows")
@AllArgsConstructor
public class UserFollowController {
	private final UserFollowService userFollowService;

	@PostMapping("/{followingId}")
	public APIResponse<UserFollowResponse> save(@PathVariable Long followingId) {
		APIResponse<UserFollowResponse> apiResponse = new APIResponse<UserFollowResponse>();
		apiResponse.setResult(userFollowService.save(followingId));
		return apiResponse;
	}

	@DeleteMapping("/{followingId}")
	public APIResponse<Void> delete(@PathVariable Long followingId) {
		userFollowService.delete(followingId);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		return apiResponse;
	}

	@GetMapping("/following")
	public APIResponse<UserFollowResponse> getFollowing() {
		APIResponse<UserFollowResponse> apiResponse = new APIResponse<UserFollowResponse>();
		apiResponse.setResultList(userFollowService.getFollowingByFollower());
		return apiResponse;
	}

	@GetMapping("/follower")
	public APIResponse<UserFollowResponse> getFollower() {
		APIResponse<UserFollowResponse> apiResponse = new APIResponse<UserFollowResponse>();
		apiResponse.setResultList(userFollowService.getFollowerByFollowing());
		return apiResponse;
	}

	@GetMapping("/my-follow-count")
	public APIResponse<FollowCountResponse> getMyFollowCount() {
		APIResponse<FollowCountResponse> apiResponse = new APIResponse<FollowCountResponse>();
		apiResponse.setResult(userFollowService.getMyFollowCount());
		return apiResponse;
	}

	@GetMapping("/follow-count/{userId}")
	public APIResponse<FollowCountResponse> getFollowCount(@PathVariable Long userId) {
		APIResponse<FollowCountResponse> apiResponse = new APIResponse<FollowCountResponse>();
		apiResponse.setResult(userFollowService.getFollowCount(userId));
		return apiResponse;
	}

	@GetMapping("/check/{userId}")
	public APIResponse<Boolean> checkFollowed(@PathVariable Long userId) {
		APIResponse<Boolean> apiResponse = new APIResponse<Boolean>();
		apiResponse.setResult(userFollowService.checkFollowed(userId));
		return apiResponse;
	}

	@GetMapping("/check-is-me/{userId}")
	public APIResponse<Boolean> checkIsMe(@PathVariable Long userId) {
		APIResponse<Boolean> apiResponse = new APIResponse<Boolean>();
		apiResponse.setResult(userFollowService.checkIsMe(userId));
		return apiResponse;
	}

}

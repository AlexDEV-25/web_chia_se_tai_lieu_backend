package com.example.app.dto.response.userfollow;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowResponse {
	private Long id;
	private Long followerId;
	private String followerName;
	private Long followingId;
	private String followingName;
	private String followingAvatar;
	private LocalDateTime createdAt;
}

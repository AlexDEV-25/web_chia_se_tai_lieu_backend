package com.example.app.dto.response.userfollow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowCountResponse {
	private Long follower;
	private Long following;
}

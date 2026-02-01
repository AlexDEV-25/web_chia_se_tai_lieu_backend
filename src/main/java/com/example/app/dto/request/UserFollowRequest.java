package com.example.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowRequest {
	@NotNull(message = "followerId không được để trống")
	private Long followerId;

	@NotNull(message = "followingId không được để trống")
	private Long followingId;

}

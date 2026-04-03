package com.example.app.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBioResponse {
	private String username;
	private String avatarUrl;
	private String bio;
}

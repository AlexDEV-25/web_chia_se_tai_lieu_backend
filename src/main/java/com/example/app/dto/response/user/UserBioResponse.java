package com.example.app.dto.response.user;

import com.example.app.constant.ConnectionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBioResponse {
	private Long id;
	private String username;
	private String avatarUrl;
	private String bio;
	private ConnectionStatus status;
}

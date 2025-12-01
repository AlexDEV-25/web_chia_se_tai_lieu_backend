package com.example.app.dto.response;

import java.time.LocalDateTime;

import com.example.app.share.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private Long id;
	private String username;
	private String email;
	private String password;
	private boolean isVerified;
	private String avatarUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Role role = Role.USER;
	private boolean hide;
}

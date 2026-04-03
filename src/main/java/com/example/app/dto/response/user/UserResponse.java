package com.example.app.dto.response.user;

import java.time.LocalDateTime;
import java.util.List;

import com.example.app.dto.response.role.RoleResponse;

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
	private String bio;
	private boolean verified;
	private String avatarUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<RoleResponse> roles;
	private boolean hide;
}

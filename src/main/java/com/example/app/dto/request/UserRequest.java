package com.example.app.dto.request;

import java.time.LocalDateTime;

import com.example.app.share.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
	private String username;
	private String email;
	private String password;
	private boolean isVerified;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Role role = Role.USER;
	private boolean hide = false;
}

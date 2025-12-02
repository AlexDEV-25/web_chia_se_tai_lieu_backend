package com.example.app.dto.request;

import java.time.LocalDateTime;
import java.util.List;

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
	private boolean verified;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<String> roles;
	private boolean hide = false;
}

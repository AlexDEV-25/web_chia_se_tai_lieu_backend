package com.example.app.dto;

import java.time.LocalDateTime;

import com.example.app.share.Role;

public class UserDTO {
	private Long id;
	private String fullName;
	private String email;
	private String password;
	private boolean isVerified;
	private String avatarUrl;
	private String avatarData;
	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt = LocalDateTime.now();
	private Role role = Role.USER;
	private boolean hide;

	public UserDTO(Long id, String fullName, String email, String password, boolean isVerified, String avatarUrl,
			String avatarData, LocalDateTime createdAt, LocalDateTime updatedAt, Role role, boolean hide) {
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.isVerified = isVerified;
		this.avatarUrl = avatarUrl;
		this.avatarData = avatarData;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.role = role;
		this.hide = hide;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getAvatarData() {
		return avatarData;
	}

	public void setAvatarData(String avatarData) {
		this.avatarData = avatarData;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

}

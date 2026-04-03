package com.example.app.dto.response.role;

import java.util.List;

import com.example.app.dto.response.permission.PermissionResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
	private String name;
	private String description;
	private List<PermissionResponse> permissions;
}

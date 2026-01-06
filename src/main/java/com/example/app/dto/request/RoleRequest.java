package com.example.app.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {

	@NotBlank(message = "name không được để trống")
	private String name;

	private String description;

	private List<String> permissions;
}

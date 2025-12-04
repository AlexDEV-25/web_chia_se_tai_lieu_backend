package com.example.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
	@NotBlank(message = "Tên danh mục không được để trống")
	@Size(min = 3, message = "Tên danh mục phải có ít nhất 3 ký tự")
	private String name;

	private String description;

	private boolean hide = false;
}

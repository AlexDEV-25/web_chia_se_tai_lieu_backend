package com.example.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserInfoRequest {
	@NotBlank(message = "Tên người dùng không được để trống")
	@Size(min = 2, max = 50, message = "Tên người dùng phải từ 2 đến 50 ký tự")
	private String username;

	@Email(message = "Email không hợp lệ")
	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email phải đúng định dạng ví dụ: example@gmail.com")
	private String email;

	private String bio;
}

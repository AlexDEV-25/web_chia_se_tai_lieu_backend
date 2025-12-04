package com.example.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	private String email;

	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
//	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Mật khẩu phải gồm chữ và số")
	private String password;
}

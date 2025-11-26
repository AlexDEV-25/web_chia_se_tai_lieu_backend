package com.example.app.controller;

import java.time.LocalDateTime;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.UserDTO;
import com.example.app.model.User;
import com.example.app.service.RegisterService;
import com.example.app.share.Role;

@RestController
@RequestMapping("api/register")
public class RegisterController {
	private final RegisterService registerService;

	public RegisterController(RegisterService registerService) {
		this.registerService = registerService;
	}

	@PostMapping
	public UserDTO userRegister(@Validated @RequestBody UserDTO dto) {
		User user = new User();
		user.setFullName(dto.getFullName());
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());
		user.setVerified(false);
		user.setAvatarUrl("");
		user.setAvatarData("");
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(null);
		user.setRole(Role.USER);
		user.setHide(false);
		System.out.println(user.toString());
		User saved = registerService.userRegister(user);
		return new UserDTO(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getPassword(),
				saved.isVerified(), saved.getAvatarUrl(), saved.getAvatarData(), saved.getCreatedAt(),
				saved.getUpdatedAt(), saved.getRole(), saved.isHide());

	}

}

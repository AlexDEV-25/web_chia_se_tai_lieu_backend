package com.example.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.model.User;
import com.example.app.repository.UserRepository;

@Service
public class RegisterService {
	private UserRepository userRepository;

	public RegisterService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public User userRegister(User user) {
		if (!userRepository.existsByEmail(user.getEmail())) {
			User data = userRepository.save(user);
			return data;
		}
		return null;
	}

}

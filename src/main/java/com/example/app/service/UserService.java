package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.HideRequest;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.UserResponse;
import com.example.app.mapper.UserMapper;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	public List<UserResponse> getAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserResponse> response = new ArrayList<UserResponse>();
		for (User u : users) {
			response.add(userMapper.userToResponse(u));
		}
		return response;
	}

	public UserResponse findById(Long id) {
		User find = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
		return userMapper.userToResponse(find);
	}

	public UserResponse save(UserRequest dto) {
		User user = userMapper.requestToUser(dto);
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User saved = userRepository.save(user);
		UserResponse response = userMapper.userToResponse(saved);
		return response;
	}

	public UserResponse update(Long id, UserRequest dto) {
		User entity = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
		userMapper.updateUser(entity, dto);
		User saved = userRepository.save(entity);
		return userMapper.userToResponse(saved);
	}

	public UserResponse hide(Long id, HideRequest dto) {
		User entity = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
		userMapper.hideUser(entity, dto);
		User saved = userRepository.save(entity);
		return userMapper.userToResponse(saved);
	}

	public UserResponse verified(Long id, UserRequest dto) {
		User entity = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
		userMapper.updateVerified(entity, dto);
		User saved = userRepository.save(entity);
		return userMapper.userToResponse(saved);
	}

	public void delete(Long id) {
		try {
			userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new RuntimeException("User not found");
		}
	}

	public boolean checkEmailExist(String email) {
		return userRepository.existsByEmail(email);
	}
}

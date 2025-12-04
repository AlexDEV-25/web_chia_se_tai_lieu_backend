package com.example.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.HideRequest;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.UserResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.UserMapper;
import com.example.app.model.Role;
import com.example.app.model.User;
import com.example.app.repository.RoleRepository;
import com.example.app.repository.UserRepository;
import com.example.app.share.FileManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.storage-directory-avatar}")
	private String avatarStorage;

	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> getAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserResponse> response = new ArrayList<UserResponse>();
		for (User u : users) {
			response.add(userMapper.userToResponse(u));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse findById(Long id) {
		User find = userRepository.findById(id)
				.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return userMapper.userToResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse save(UserRequest dto) {
		User user = userMapper.requestToUser(dto);

		List<Role> roles = roleRepository.findAllById(dto.getRoles());
		user.setRoles(roles);

		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new AppException("email đã tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
		if (userRepository.existsByUsername(dto.getEmail())) {
			throw new AppException("username đã tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User saved = userRepository.save(user);
		UserResponse response = userMapper.userToResponse(saved);
		return response;

	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse update(Long id, MultipartFile avt, UserRequest dto) throws IOException {
		User entity = userRepository.findById(id)
				.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		List<Role> roles = roleRepository.findAllById(dto.getRoles());
		entity.setRoles(roles);

		FileManager fileStorage = new FileManager();
		if (entity.getAvatarUrl() != null) {
			fileStorage.deleteFile(avatarStorage + "\\" + entity.getAvatarUrl());
		}
		String fileUrl = fileStorage.saveFile(avt, avatarStorage);
		entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		userMapper.updateUser(entity, dto);
		entity.setAvatarUrl(fileUrl);
		User saved = userRepository.save(entity);
		return userMapper.userToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse hide(Long id, HideRequest dto) {
		User entity = userRepository.findById(id)
				.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		userMapper.hideUser(entity, dto);
		User saved = userRepository.save(entity);
		return userMapper.userToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('GET_INFO')")
	public UserResponse getMyInfo() {
		User info = getUserByToken();
		return userMapper.userToResponse(info);
	}

	@PreAuthorize("hasAuthority('UPDATE_INFO')")
	public UserResponse updateMyinfo(MultipartFile avt, UserRequest dto) throws IOException {
		User entity = getUserByToken();
		List<Role> roles = roleRepository.findAllById(dto.getRoles());
		entity.setRoles(roles);

		FileManager fileStorage = new FileManager();
		if (entity.getAvatarUrl() != null) {
			fileStorage.deleteFile(avatarStorage + "\\" + entity.getAvatarUrl());
		}
		String fileUrl = fileStorage.saveFile(avt, avatarStorage);
		entity.setPassword(passwordEncoder.encode(entity.getPassword()));
		userMapper.updateUser(entity, dto);
		entity.setAvatarUrl(fileUrl);
		User saved = userRepository.save(entity);
		return userMapper.userToResponse(saved);
	}

// tính sau vì còn phải viết gửi mail
	public UserResponse verified(Long id, UserRequest dto) {
		User entity = userRepository.findById(id)
				.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		userMapper.updateVerified(entity, dto);
		User saved = userRepository.save(entity);
		return userMapper.userToResponse(saved);
	}

	private User getUserByToken() {
		SecurityContext context = SecurityContextHolder.getContext();
		System.out.println(context.toString());
		String name = context.getAuthentication().getName();
		User entity = userRepository.findByUsername(name);
		return entity;
	}
}

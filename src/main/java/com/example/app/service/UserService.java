package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.constant.ConnectionStatus;
import com.example.app.constant.HideType;
import com.example.app.dto.request.ChangePasswordRequest;
import com.example.app.dto.request.ChangeUserInfoRequest;
import com.example.app.dto.request.DisplayRequest;
import com.example.app.dto.request.UserRequest;
import com.example.app.dto.response.user.UserBioResponse;
import com.example.app.dto.response.user.UserResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.FileManager;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.UserMapper;
import com.example.app.model.Role;
import com.example.app.model.User;
import com.example.app.repository.RoleRepository;
import com.example.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final GetUserByToken getUserByToken;
	private final FileManager fileStorage;

	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> getAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserResponse> response = users.stream().map(userMapper::userToResponse).toList();
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse save(UserRequest dto) {
		User user = userMapper.requestToUser(dto);

		List<Role> roles = roleRepository.findAllById(dto.getRoles());
		user.setRoles(roles);
		user.setCreatedAt(LocalDateTime.now());
		if (this.checkEmailExists(dto.getEmail())) {
			throw new AppException("email đã tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
		if (this.checkUsernameExists(dto.getEmail())) {
			throw new AppException("username đã tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User saved = userRepository.save(user);
		UserResponse response = userMapper.userToResponse(saved);
		return response;

	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse hide(Long id, DisplayRequest request) {
		if (request.getType() == HideType.USER) {
			User entity = userRepository.findById(id)
					.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			entity.setHide(request.isHide());
			entity.setUpdatedAt(LocalDateTime.now());
			User saved = userRepository.save(entity);
			return userMapper.userToResponse(saved);
		} else {
			throw new AppException("không đúng type", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('GET_MY_INFO')")
	public UserResponse getMyInfo() {
		User info = getUserByToken.get();
		return userMapper.userToResponse(info);
	}

	@PreAuthorize("hasAuthority('UPDATE_MY_INFO')")
	public UserResponse updateMyinfo(MultipartFile avt, ChangeUserInfoRequest dto) {
		User entity = getUserByToken.get();
		entity.setUpdatedAt(LocalDateTime.now());
		if (avt != null) {
			try {
				Map<?, ?> handleAvt = fileStorage.uploadImage(avt);
				String avatarUrl = (String) handleAvt.get("secure_url");

				entity.setAvatarUrl(avatarUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		userMapper.updateUserInfo(entity, dto);

		User saved = userRepository.save(entity);
		return userMapper.userToResponse(saved);
	}

	@PreAuthorize("hasAuthority('SEARCH_USER')")
	public List<UserBioResponse> search(String keyword) {
		return userRepository.search(keyword);
	}

	@PreAuthorize("hasAuthority('CHANGE_PASSWORD')")
	public void changePassword(ChangePasswordRequest request) {
		User entity = getUserByToken.get();
		entity.setPassword(passwordEncoder.encode(request.getPassword()));
		userRepository.save(entity);
	}

	public boolean checkEmailExists(String email) {
		return userRepository.existsByEmail(email);
	}

	public boolean checkUsernameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	public UserBioResponse getUserInfo(Long id) {
		User find = userRepository.findByIdAndHideFalse(id)
				.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return userMapper.userToUserBioResponse(find);
	}

	public void changeConnectStatus(String username, ConnectionStatus status) {
		User find = userRepository.findByUsername(username);
		find.setStatus(status);
		userRepository.save(find);
	}

}

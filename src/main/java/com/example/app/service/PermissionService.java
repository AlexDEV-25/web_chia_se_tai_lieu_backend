package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.PermissionRequest;
import com.example.app.dto.response.PermissionResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.PermissionMapper;
import com.example.app.model.Permission;
import com.example.app.repository.PermissionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PermissionService {
	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;

	@PreAuthorize("hasRole('ADMIN')")
	public List<PermissionResponse> getAllPermissions() {
		List<Permission> permissions = permissionRepository.findAll();
		List<PermissionResponse> responses = new ArrayList<PermissionResponse>();
		for (Permission p : permissions) {
			responses.add(permissionMapper.permissionToResponse(p));
		}
		return responses;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public PermissionResponse findById(String name) {
		Permission find = permissionRepository.findById(name)
				.orElseThrow(() -> new AppException("permission không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return permissionMapper.permissionToResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public PermissionResponse save(PermissionRequest dto) {
		Permission permission = permissionMapper.requestToPermission(dto);
		Permission saved = permissionRepository.save(permission);
		PermissionResponse response = permissionMapper.permissionToResponse(saved);
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public PermissionResponse update(String name, PermissionRequest dto) {
		Permission entity = permissionRepository.findById(name)
				.orElseThrow(() -> new AppException("permission không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		permissionMapper.updatePermission(entity, dto);
		Permission saved = permissionRepository.save(entity);
		return permissionMapper.permissionToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(String name) {
		try {
			permissionRepository.deleteById(name);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("permission không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}
}

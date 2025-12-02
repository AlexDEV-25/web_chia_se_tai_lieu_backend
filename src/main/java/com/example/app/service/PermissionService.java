package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.PermissionRequest;
import com.example.app.dto.response.PermissionResponse;
import com.example.app.mapper.PermissionMapper;
import com.example.app.model.Permission;
import com.example.app.repository.PermissionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PermissionService {
	private final PermissionRepository permissionRepository;
	private final PermissionMapper permissionMapper;

	public List<PermissionResponse> getAllCategories() {
		List<Permission> permissions = permissionRepository.findAll();
		List<PermissionResponse> responses = new ArrayList<PermissionResponse>();
		for (Permission p : permissions) {
			responses.add(permissionMapper.permissionToResponse(p));
		}
		return responses;
	}

	public PermissionResponse findById(String name) {
		Permission find = permissionRepository.findById(name)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy Permission"));
		return permissionMapper.permissionToResponse(find);
	}

	public PermissionResponse save(PermissionRequest dto) {
		Permission permission = permissionMapper.requestToPermission(dto);
		Permission saved = permissionRepository.save(permission);
		PermissionResponse response = permissionMapper.permissionToResponse(saved);
		return response;
	}

	public PermissionResponse update(String name, PermissionRequest dto) {
		Permission entity = permissionRepository.findById(name)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy Permission"));
		permissionMapper.updatePermission(entity, dto);
		Permission saved = permissionRepository.save(entity);
		return permissionMapper.permissionToResponse(saved);
	}

	public void delete(String name) {
		try {
			permissionRepository.deleteById(name);
		} catch (EmptyResultDataAccessException e) {
			throw new RuntimeException("Permission not found");
		}
	}
}

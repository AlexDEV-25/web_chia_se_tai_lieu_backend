package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.RoleRequest;
import com.example.app.dto.response.RoleResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.RoleMapper;
import com.example.app.model.Permission;
import com.example.app.model.Role;
import com.example.app.repository.PermissionRepository;
import com.example.app.repository.RoleRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleService {
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	private final RoleMapper roleMapper;

	@PreAuthorize("hasRole('ADMIN')")
	public List<RoleResponse> getAllCategories() {
		List<Role> roles = roleRepository.findAll();
		List<RoleResponse> responses = new ArrayList<RoleResponse>();
		for (Role r : roles) {
			responses.add(roleMapper.roleToResponse(r));
		}
		return responses;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public RoleResponse findById(String name) {
		Role find = roleRepository.findById(name)
				.orElseThrow(() -> new AppException("role không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return roleMapper.roleToResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public RoleResponse save(RoleRequest dto) {
		Role role = roleMapper.requestToRole(dto);
		List<Permission> pemissions = permissionRepository.findAllById(dto.getPermissions());
		role.setPermissions(pemissions);
		Role saved = roleRepository.save(role);
		RoleResponse response = roleMapper.roleToResponse(saved);
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public RoleResponse update(String name, RoleRequest dto) {
		Role entity = roleRepository.findById(name)
				.orElseThrow(() -> new AppException("role không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		roleMapper.updateRole(entity, dto);
		Role saved = roleRepository.save(entity);
		return roleMapper.roleToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(String name) {
		try {
			roleRepository.deleteById(name);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("role không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}
}

package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.RoleRequest;
import com.example.app.dto.response.RoleResponse;
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

	public List<RoleResponse> getAllCategories() {
		List<Role> roles = roleRepository.findAll();
		List<RoleResponse> responses = new ArrayList<RoleResponse>();
		for (Role r : roles) {
			responses.add(roleMapper.roleToResponse(r));
		}
		return responses;
	}

	public RoleResponse findById(String name) {
		Role find = roleRepository.findById(name).orElseThrow(() -> new RuntimeException("Không tìm thấy role"));
		return roleMapper.roleToResponse(find);
	}

	public RoleResponse save(RoleRequest dto) {
		Role role = roleMapper.requestToRole(dto);
		List<Permission> pemissions = permissionRepository.findAllById(dto.getPermissions());
		role.setPermissions(pemissions);
		Role saved = roleRepository.save(role);
		RoleResponse response = roleMapper.roleToResponse(saved);
		return response;
	}

	public RoleResponse update(String name, RoleRequest dto) {
		Role entity = roleRepository.findById(name).orElseThrow(() -> new RuntimeException("Không tìm thấy role"));
		roleMapper.updateRole(entity, dto);
		Role saved = roleRepository.save(entity);
		return roleMapper.roleToResponse(saved);
	}

	public void delete(String name) {
		try {
			roleRepository.deleteById(name);
		} catch (EmptyResultDataAccessException e) {
			throw new RuntimeException("role not found");
		}
	}
}

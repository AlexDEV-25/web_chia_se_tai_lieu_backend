package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.RoleRequest;
import com.example.app.dto.response.RoleResponse;
import com.example.app.model.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	@Mapping(target = "permissions", ignore = true)
	Role requestToRole(RoleRequest Request);

	RoleResponse roleToResponse(Role entity);

	@Mapping(target = "permissions", ignore = true)
	void updateRole(@MappingTarget Role role, RoleRequest Requsest);

}

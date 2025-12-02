package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.app.dto.request.PermissionRequest;
import com.example.app.dto.response.PermissionResponse;
import com.example.app.model.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

	Permission requestToPermission(PermissionRequest Request);

	Permission responseToPermission(PermissionResponse Response);

	PermissionResponse permissionToResponse(Permission entity);

	void updatePermission(@MappingTarget Permission permission, PermissionRequest Requsest);
}

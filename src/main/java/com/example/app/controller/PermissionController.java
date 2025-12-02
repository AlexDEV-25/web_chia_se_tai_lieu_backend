package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.PermissionRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.PermissionResponse;
import com.example.app.service.PermissionService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/permissions")
@AllArgsConstructor
public class PermissionController {
	private final PermissionService permissionService;

	@GetMapping("/{name}")
	public APIResponse<PermissionResponse> getById(@PathVariable String name) {
		PermissionResponse response = permissionService.findById(name);
		APIResponse<PermissionResponse> apiResponse = new APIResponse<PermissionResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<PermissionResponse> getAll() {
		List<PermissionResponse> response = permissionService.getAllCategories();
		APIResponse<PermissionResponse> apiResponse = new APIResponse<PermissionResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PostMapping
	public APIResponse<PermissionResponse> create(@RequestBody PermissionRequest dto) {
		PermissionResponse response = permissionService.save(dto);
		APIResponse<PermissionResponse> apiResponse = new APIResponse<PermissionResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PutMapping("/{name}")
	public APIResponse<PermissionResponse> update(@PathVariable String name, @RequestBody PermissionRequest dto) {
		PermissionResponse response = permissionService.update(name, dto);
		APIResponse<PermissionResponse> apiResponse = new APIResponse<PermissionResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@DeleteMapping("/{name}")
	public APIResponse<PermissionResponse> delete(@PathVariable String name) {
		permissionService.delete(name);
		APIResponse<PermissionResponse> apiResponse = new APIResponse<PermissionResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}
}

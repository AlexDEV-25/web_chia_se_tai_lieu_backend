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

import com.example.app.dto.request.RoleRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.RoleResponse;
import com.example.app.service.RoleService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
public class RoleController {
	private final RoleService roleService;

	@GetMapping("/{name}")
	public APIResponse<RoleResponse> getById(@PathVariable String name) {
		RoleResponse response = roleService.findById(name);
		APIResponse<RoleResponse> apiResponse = new APIResponse<RoleResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<RoleResponse> getAll() {
		List<RoleResponse> response = roleService.getAllCategories();
		APIResponse<RoleResponse> apiResponse = new APIResponse<RoleResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PostMapping
	public APIResponse<RoleResponse> create(@RequestBody RoleRequest dto) {
		RoleResponse response = roleService.save(dto);
		APIResponse<RoleResponse> apiResponse = new APIResponse<RoleResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PutMapping("/{name}")
	public APIResponse<RoleResponse> update(@PathVariable String name, @RequestBody RoleRequest dto) {
		RoleResponse response = roleService.update(name, dto);
		APIResponse<RoleResponse> apiResponse = new APIResponse<RoleResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@DeleteMapping("/{name}")
	public APIResponse<RoleResponse> delete(@PathVariable String name) {
		roleService.delete(name);
		APIResponse<RoleResponse> apiResponse = new APIResponse<RoleResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}
}

package com.example.app.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.DocumentRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.document.DocumentAdminResponse;
import com.example.app.dto.response.document.DocumentDetailResponse;
import com.example.app.service.DocumentService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/documents/admin")
@AllArgsConstructor
public class AdminDocumentController {
	private final DocumentService documentService;

	@GetMapping("/{id}")
	public APIResponse<DocumentDetailResponse> getById(@PathVariable Long id) {
		DocumentDetailResponse response = documentService.findById(id);
		APIResponse<DocumentDetailResponse> apiResponse = new APIResponse<DocumentDetailResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<DocumentAdminResponse> getAll() {
		List<DocumentAdminResponse> response = documentService.findAll();
		APIResponse<DocumentAdminResponse> apiResponse = new APIResponse<DocumentAdminResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<Void> delete(@PathVariable Long id) {
		documentService.delete(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<DocumentDetailResponse> update(@PathVariable Long id, @RequestBody DocumentRequest dto) {
		DocumentDetailResponse response = documentService.update(id, dto);
		APIResponse<DocumentDetailResponse> apiResponse = new APIResponse<DocumentDetailResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

}

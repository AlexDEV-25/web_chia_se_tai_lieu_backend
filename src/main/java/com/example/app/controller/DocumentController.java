package com.example.app.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.DocumentRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.document.DocumentDetailResponse;
import com.example.app.dto.response.document.DocumentResponse;
import com.example.app.dto.response.document.DocumentStatsResponse;
import com.example.app.dto.response.document.DocumentUserResponse;
import com.example.app.exception.AppException;
import com.example.app.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/documents")
@AllArgsConstructor
public class DocumentController {
	private final DocumentService documentService;

	@GetMapping("/stats")
	public APIResponse<DocumentStatsResponse> getStats() {
		DocumentStatsResponse response = documentService.getStats();
		APIResponse<DocumentStatsResponse> apiResponse = new APIResponse<DocumentStatsResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("success");
		return apiResponse;
	}

	@GetMapping("/search")
	public APIResponse<DocumentResponse> search(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long categoryId) {
		List<DocumentResponse> response = documentService.search(keyword, categoryId);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/{id}")
	public APIResponse<DocumentDetailResponse> getByIdPublicDocument(@PathVariable Long id) {
		DocumentDetailResponse response = documentService.findByIdPublicDocument(id);
		APIResponse<DocumentDetailResponse> apiResponse = new APIResponse<DocumentDetailResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<DocumentResponse> getAllPublicDocuments() {
		List<DocumentResponse> response = documentService.getAllPublicDocuments();
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/user")
	public APIResponse<DocumentResponse> getByUser(@RequestParam Long documentId, @RequestParam Long userId) {
		List<DocumentResponse> response = documentService.getDocumentsByUser(userId, documentId);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/category")
	public APIResponse<DocumentResponse> getByCategory(@RequestParam Long categoryId, @RequestParam Long documentId) {
		List<DocumentResponse> response = documentService.getDocumentsByCategory(categoryId, documentId);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/user/{userId}")
	public APIResponse<DocumentResponse> getAllDocumentsByUser(@PathVariable Long userId) {
		List<DocumentResponse> response = documentService.getAllDocumentsByUser(userId);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/count/{userId}")
	public APIResponse<Long> countDocumentOfUser(@PathVariable Long userId) {
		Long num = documentService.countDocumentOfUser(userId);
		APIResponse<Long> apiResponse = new APIResponse<Long>();
		apiResponse.setResult(num);
		apiResponse.setMessage("success");
		return apiResponse;
	}

	@PostMapping("/view/{id}")
	public APIResponse<Void> increaseView(@PathVariable Long id) {
		documentService.increaseView(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("increase success");
		return apiResponse;
	}

	@PostMapping("/download/{id}")
	public APIResponse<Void> increaseDownload(@PathVariable Long id) {
		documentService.increaseDownload(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("increase success");
		return apiResponse;
	}

	@PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public APIResponse<DocumentDetailResponse> create(@RequestPart("file") MultipartFile file,
			@RequestPart("data") String dataJson) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			DocumentRequest dto = mapper.readValue(dataJson, DocumentRequest.class);

			DocumentDetailResponse response = documentService.uploadFile(file, dto);
			APIResponse<DocumentDetailResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thành công");
			apiResponse.setResult(response);

			return apiResponse;

		} catch (Exception e) {
			throw new AppException("Upload Thất bại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/download")
	public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception {

		FileResponse file = documentService.downloadById(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
				.body(file.getResource());
	}

	@GetMapping("/my-document")
	public APIResponse<DocumentUserResponse> getMyDocument() {
		List<DocumentUserResponse> response = documentService.getMyDocument();
		APIResponse<DocumentUserResponse> apiResponse = new APIResponse<DocumentUserResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/my-document/{id}")
	public APIResponse<DocumentDetailResponse> getMyDocumentDetail(@PathVariable Long id) {
		DocumentDetailResponse response = documentService.getMyDocumentDetail(id);
		APIResponse<DocumentDetailResponse> apiResponse = new APIResponse<DocumentDetailResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("/my-document/{id}")
	public APIResponse<DocumentUserResponse> updateMyDocument(@PathVariable Long id,
			@RequestBody @Valid DocumentRequest dto) {
		DocumentUserResponse response = documentService.updateMyDocument(id, dto);
		APIResponse<DocumentUserResponse> apiResponse = new APIResponse<DocumentUserResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@DeleteMapping("/my-document/{id}")
	public APIResponse<Void> deleteMyDocument(@PathVariable Long id) {
		documentService.deleteMyDocument(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@GetMapping("/my-document/count")
	public APIResponse<Long> countMyDocument() {
		Long response = documentService.countMyDocument();
		APIResponse<Long> apiResponse = new APIResponse<Long>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

}

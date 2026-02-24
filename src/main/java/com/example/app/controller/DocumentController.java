package com.example.app.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.ContentRatingSummaryResponse;
import com.example.app.dto.response.ContentReportSummaryResponse;
import com.example.app.dto.response.DocumentFavoriteResponse;
import com.example.app.dto.response.DocumentResponse;
import com.example.app.dto.response.DocumentStatsResponse;
import com.example.app.dto.response.FileResponse;
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
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping("/search")
	public APIResponse<DocumentFavoriteResponse> search(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long categoryId) {
		List<DocumentFavoriteResponse> response = documentService.search(keyword, categoryId);
		APIResponse<DocumentFavoriteResponse> apiResponse = new APIResponse<DocumentFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/admin/{id}")
	public APIResponse<DocumentResponse> getById(@PathVariable Long id) {
		DocumentResponse response = documentService.findById(id);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping("/{id}")
	public APIResponse<DocumentResponse> getByIdPublicDocument(@PathVariable Long id) {
		DocumentResponse response = documentService.findByIdPublicDocument(id);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<DocumentFavoriteResponse> getAllPublicDocuments() {
		List<DocumentFavoriteResponse> response = documentService.getAllPublicDocumentsCheckFavorite();
		APIResponse<DocumentFavoriteResponse> apiResponse = new APIResponse<DocumentFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/user")
	public APIResponse<DocumentFavoriteResponse> getByUser(@RequestParam Long documentId, @RequestParam Long userId) {
		List<DocumentFavoriteResponse> response = documentService.getDocumentsByUserCheckFavorite(userId, documentId);
		APIResponse<DocumentFavoriteResponse> apiResponse = new APIResponse<DocumentFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/category")
	public APIResponse<DocumentFavoriteResponse> getByCategory(@RequestParam Long categoryId,
			@RequestParam Long documentId) {
		List<DocumentFavoriteResponse> response = documentService.getDocumentsByCategoryCheckFavorite(categoryId,
				documentId);
		APIResponse<DocumentFavoriteResponse> apiResponse = new APIResponse<DocumentFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/user/{userId}")
	public APIResponse<DocumentFavoriteResponse> getAllDocumentsByUser(@PathVariable Long userId) {
		List<DocumentFavoriteResponse> response = documentService.getAllDocumentsByUserheckFavorite(userId);
		APIResponse<DocumentFavoriteResponse> apiResponse = new APIResponse<DocumentFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/admin")
	public APIResponse<DocumentResponse> getAll() {
		List<DocumentResponse> response = documentService.getAllDocuments();
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/admin/rating")
	public APIResponse<ContentRatingSummaryResponse> getAllDocumentRatingSummary() {
		List<ContentRatingSummaryResponse> response = documentService.getAllDocumentRatingSummary();
		APIResponse<ContentRatingSummaryResponse> apiResponse = new APIResponse<ContentRatingSummaryResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/admin/report")
	public APIResponse<ContentReportSummaryResponse> getAllDocumentReportSummary() {
		List<ContentReportSummaryResponse> response = documentService.getAllDocumentReportSummary();
		APIResponse<ContentReportSummaryResponse> apiResponse = new APIResponse<ContentReportSummaryResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@DeleteMapping("/admin/{id}")
	public APIResponse<DocumentResponse> delete(@PathVariable Long id) {
		documentService.delete(id);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@PutMapping("/admin/hide/{id}")
	public APIResponse<DocumentResponse> hide(@PathVariable Long id, @RequestBody @Valid HideRequest dto) {
		DocumentResponse response = documentService.hide(id, dto);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}

	@PutMapping("/admin/{id}")
	public APIResponse<DocumentResponse> update(@PathVariable Long id, @RequestBody DocumentRequest dto) {
		DocumentResponse response = documentService.update(id, dto);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
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

	@GetMapping("/count/{userId}")
	public APIResponse<Long> countDocumentOfUser(@PathVariable Long userId) {
		Long num = documentService.countDocumentOfUser(userId);
		APIResponse<Long> apiResponse = new APIResponse<Long>();
		apiResponse.setResult(num);
		apiResponse.setMessage("increase success");
		return apiResponse;
	}

	@PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public APIResponse<DocumentResponse> create(@RequestPart("file") MultipartFile file,
			@RequestPart("data") String dataJson) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			DocumentRequest dto = mapper.readValue(dataJson, DocumentRequest.class);
			System.out.println(dto.toString());
			DocumentResponse response = documentService.uploadFile(file, dto);
			APIResponse<DocumentResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thành công");
			apiResponse.setResult(response);
			System.out.println(apiResponse.getResult().toString());
			return apiResponse;

		} catch (Exception e) {
			APIResponse<DocumentResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thất bại: " + e.getMessage());
			return apiResponse;
		}
	}

	@GetMapping("/{id}/download")
	public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {

		FileResponse file = documentService.downloadById(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
				.body(file.getResource());
	}

	@GetMapping("/{id}/file")
	public ResponseEntity<Resource> loadPublicDocument(@PathVariable Long id) throws IOException {

		FileResponse file = documentService.loadPublicDocumentFile(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.body(file.getResource());
	}

	@GetMapping("/admin/{id}/file")
	public ResponseEntity<Resource> loadAnyDocumentFile(@PathVariable Long id) throws IOException {

		FileResponse file = documentService.loadAnyDocumentFile(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.body(file.getResource());
	}

	@GetMapping("/my-document")
	public APIResponse<DocumentResponse> getMyDocument() {
		List<DocumentResponse> response = documentService.getMyDocument();
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("/my-document/{id}")
	public APIResponse<DocumentResponse> updateMyDocument(@PathVariable Long id,
			@RequestBody @Valid DocumentRequest dto) {
		DocumentResponse response = documentService.updateMyDocument(id, dto);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@DeleteMapping("/my-document/{id}")
	public APIResponse<DocumentResponse> deleteMyDocument(@PathVariable Long id) {
		documentService.deleteMyDocument(id);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@GetMapping("/my-document/count")
	public APIResponse<Long> CountMyDocument() {
		Long response = documentService.countMyDocument();
		APIResponse<Long> apiResponse = new APIResponse<Long>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

}

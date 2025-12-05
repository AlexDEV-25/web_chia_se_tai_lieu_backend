package com.example.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
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
import com.example.app.dto.response.DocumentResponse;
import com.example.app.dto.response.FileResponse;
import com.example.app.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/documents")
@AllArgsConstructor
public class DocumentController {
	private final DocumentService documentService;

	@GetMapping("/{id}")
	public APIResponse<DocumentResponse> getById(@PathVariable Long id) {
		DocumentResponse response = documentService.findById(id);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<DocumentResponse> getAll() {
		List<DocumentResponse> response = documentService.getAlldocuments();
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<DocumentResponse> delete(@PathVariable Long id) {
		documentService.delete(id);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<DocumentResponse> update(@PathVariable Long id, @RequestBody DocumentRequest dto) {
		DocumentResponse response = documentService.update(id, dto);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@GetMapping("/user/{userId}")
	public APIResponse<DocumentResponse> getByUser(@PathVariable Long userId) {
		List<DocumentResponse> response = documentService.getByUser(userId);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/category/{categoryId}")
	public APIResponse<DocumentResponse> getByCategory(@PathVariable Long categoryId) {
		List<DocumentResponse> response = documentService.getByCategory(categoryId);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("hide/{id}")
	public APIResponse<DocumentResponse> hide(@PathVariable Long id, @RequestBody HideRequest dto) {
		DocumentResponse response = documentService.hide(id, dto);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}

	@PutMapping("status/{id}")
	public APIResponse<DocumentResponse> changeStatus(@PathVariable Long id, @RequestBody DocumentRequest dto) {
		DocumentResponse response = documentService.changeStatus(id, dto);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}

	@PostMapping("/view/{id}")
	public APIResponse<DocumentResponse> increaseView(@PathVariable Long id) {
		documentService.increaseView(id);
		APIResponse<DocumentResponse> apiResponse = new APIResponse<DocumentResponse>();
		apiResponse.setMessage("increase success");
		return apiResponse;
	}

	@PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public APIResponse<DocumentResponse> create(@RequestPart("file") MultipartFile file,
			@RequestPart("img") MultipartFile img, @RequestPart("data") String dataJson // nhận JSON dạng chuỗi
	) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			DocumentRequest dto = mapper.readValue(dataJson, DocumentRequest.class);

			DocumentResponse response = documentService.uploadFile(file, img, dto);
			APIResponse<DocumentResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thành công");
			apiResponse.setResult(response);
			return apiResponse;

		} catch (Exception e) {
			APIResponse<DocumentResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thất bại: " + e.getMessage());
			return apiResponse;
		}
	}

	@GetMapping(value = "/download-file")
	public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String filename) {
		try {
			File fileToDownload = documentService.getDownloadFile(filename);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.contentLength(fileToDownload.length()).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/{id}/file")
	public ResponseEntity<Resource> loadDocument(@PathVariable Long id) throws IOException {

		FileResponse file = documentService.loadDocumentFile(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.body(file.getResource());
	}

}

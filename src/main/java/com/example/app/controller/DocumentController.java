package com.example.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.model.Document;
import com.example.app.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@GetMapping
	public List<Document> getAll() {
		return documentService.getAll();
	}

	@GetMapping("/{id}")
	public Document getById(@PathVariable Long id) {
		return documentService.getById(id).orElse(null);
	}

	@PostMapping
	public Document create(@RequestBody Document document) {
		return documentService.save(document);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		documentService.delete(id);
	}

	@GetMapping("/user/{userId}")
	public List<Document> getByUser(@PathVariable Long userId) {
		return documentService.getByUser(userId);
	}

	@GetMapping("/category/{categoryId}")
	public List<Document> getByCategory(@PathVariable Long categoryId) {
		return documentService.getByCategory(categoryId);
	}

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
		try {
			String filename = documentService.uploadFile(file);
			return ResponseEntity.ok("File uploaded successfully: " + filename);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
		}
	}
}

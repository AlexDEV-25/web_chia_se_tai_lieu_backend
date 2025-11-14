package com.example.app.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.model.Document;
import com.example.app.repository.DocumentRepository;

@Service
public class DocumentService {
	private final DocumentRepository documentRepository;
	private final String uploadDir = "uploads/";

	public DocumentService(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	public List<Document> getAll() {
		return documentRepository.findAll();
	}

	public Optional<Document> getById(Long id) {
		return documentRepository.findById(id);
	}

	public Document save(Document document) {
		return documentRepository.save(document);
	}

	public void delete(Long id) {
		documentRepository.deleteById(id);
	}

	public List<Document> getByUser(Long userId) {
		return documentRepository.findByUserId(userId);
	}

	public List<Document> getByCategory(Long categoryId) {
		return documentRepository.findByCategoryId(categoryId);
	}

	// Upload file
	public String uploadFile(MultipartFile file) throws IOException {
		File dir = new File(uploadDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String originalFilename = file.getOriginalFilename();
		String safeFilename = UUID.randomUUID() + "_"
				+ (originalFilename != null ? Paths.get(originalFilename).getFileName().toString() : "file");

		Path filePath = Paths.get(uploadDir).resolve(safeFilename);

		try (InputStream in = file.getInputStream()) {
			Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
		}

		return safeFilename;
	}
}

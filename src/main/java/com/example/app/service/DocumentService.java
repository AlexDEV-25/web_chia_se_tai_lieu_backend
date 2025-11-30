package com.example.app.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.DocumentRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.DocumentResponse;
import com.example.app.mapper.DocumentMapper;
import com.example.app.model.Document;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class DocumentService {
	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private DocumentMapper documentMapper;
	public static final String STORAGE_DIRECTORY = "G:\\web_DATN\\storage";

	public List<DocumentResponse> getAlldocuments() {
		List<Document> documents = documentRepository.findAll();
		List<DocumentResponse> response = new ArrayList<DocumentResponse>();
		for (Document d : documents) {
			response.add(documentMapper.documentToResponse(d));
		}
		return response;
	}

	public DocumentResponse findById(Long id) {
		Document find = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
		find.setViewsCount(find.getViewsCount() + 1);
		documentRepository.save(find);
		return documentMapper.documentToResponse(find);
	}

	public void delete(Long id) {
		if (!documentRepository.existsById(id)) {
			throw new RuntimeException("Document not found with id: " + id);
		}
		documentRepository.deleteById(id);
	}

	public DocumentResponse hide(Long id, HideRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
		documentMapper.hideDocument(entity, dto);
		Document saved = documentRepository.save(entity);
		return documentMapper.documentToResponse(saved);
	}

	public void increaseDownload(Long id, DocumentRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
		documentMapper.updateDownloadCount(entity, dto);
		Document saved = documentRepository.save(entity);
		documentMapper.documentToResponse(saved);
	}

	public DocumentResponse status(Long id, DocumentRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
		documentMapper.updateStatus(entity, dto);
		Document saved = documentRepository.save(entity);
		return documentMapper.documentToResponse(saved);
	}

	public List<DocumentResponse> getByUser(Long userId) {
		List<Document> documents = documentRepository.findByUserId(userId);
		List<DocumentResponse> response = new ArrayList<DocumentResponse>();
		for (Document d : documents) {
			response.add(documentMapper.documentToResponse(d));
		}
		return response;
	}

	public List<DocumentResponse> getByCategory(Long categoryId) {
		List<Document> documents = documentRepository.findByCategoryId(categoryId);
		List<DocumentResponse> response = new ArrayList<DocumentResponse>();
		for (Document d : documents) {
			response.add(documentMapper.documentToResponse(d));
		}
		return response;
	}

	public DocumentResponse uploadFile(MultipartFile fileToSave, DocumentRequest dto) throws IOException {
		String fileUrl = this.saveFile(fileToSave);
		Document document = documentMapper.requestToDocument(dto);
		document.setFileUrl(fileUrl);
		Document saved = documentRepository.save(document);
		DocumentResponse response = documentMapper.documentToResponse(saved);
		return response;
	}

	public String saveFile(MultipartFile fileToSave) throws IOException {
		if (fileToSave == null) {
			throw new NullPointerException("fileToSave is null");
		}
		String originalFilename = fileToSave.getOriginalFilename();
		String safeFilename = UUID.randomUUID() + "_"
				+ (originalFilename != null ? Paths.get(originalFilename).getFileName().toString() : "file");

		var targetFile = new File(STORAGE_DIRECTORY + File.separator + fileToSave.getOriginalFilename());
		if (!Objects.equals(targetFile.getParent(), STORAGE_DIRECTORY)) {
			throw new SecurityException("Unsupported filename!");
		}
		Files.copy(fileToSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return safeFilename;
	}

	public File getDownloadFile(String fileName) throws Exception {
		if (fileName == null) {
			throw new NullPointerException("fileName is null");
		}
		var fileToDownload = new File(STORAGE_DIRECTORY + File.separator + fileName);
		if (!Objects.equals(fileToDownload.getParent(), STORAGE_DIRECTORY)) {
			throw new SecurityException("Unsupported filename!");
		}
		if (!fileToDownload.exists()) {
			throw new FileNotFoundException("No file named: " + fileName);
		}
		return fileToDownload;
	}
}

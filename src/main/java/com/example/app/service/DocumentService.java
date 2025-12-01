package com.example.app.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.DocumentRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.DocumentResponse;
import com.example.app.mapper.DocumentMapper;
import com.example.app.model.Category;
import com.example.app.model.Document;
import com.example.app.model.User;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.UserRepository;
import com.example.app.share.FileStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final DocumentMapper documentMapper;

	@Value("${app.storage-directory-document}")
	private String documentStorage;

	@Value("${app.storage-directory-image}")
	private String thumbnailStorage;

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
		return documentMapper.documentToResponse(find);
	}

	public void delete(Long id) {
		try {
			documentRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new RuntimeException("Document not found");
		}
	}

	public DocumentResponse update(Long id, DocumentRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
		documentMapper.updateDocument(entity, dto);
		Document saved = documentRepository.save(entity);
		return documentMapper.documentToResponse(saved);
	}

	public DocumentResponse hide(Long id, HideRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
		documentMapper.hideDocument(entity, dto);
		Document saved = documentRepository.save(entity);
		return documentMapper.documentToResponse(saved);
	}

	public DocumentResponse changeStatus(Long id, DocumentRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
		documentMapper.updateStatus(entity, dto);
		Document saved = documentRepository.save(entity);
		return documentMapper.documentToResponse(saved);
	}

	// Hàm riêng để tăng viewsCount
	public void increaseView(Long id) {
		if (id == null)
			return;
		documentRepository.findById(id).ifPresent(entity -> {
			entity.setViewsCount(entity.getViewsCount() + 1);
			documentRepository.save(entity);
		});
	}

	public void increaseDownload(Long id) {

		documentRepository.findById(id).ifPresent(entity -> {
			entity.setDownloadsCount(entity.getDownloadsCount() + 1);
			documentRepository.save(entity);
		});
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

	public DocumentResponse uploadFile(MultipartFile fileToSave, MultipartFile img, DocumentRequest dto)
			throws IOException {
		FileStorage fileStorage = new FileStorage();

		String fileUrl = fileStorage.saveFile(fileToSave, documentStorage);
		String thumbnailUrl = fileStorage.saveFile(img, thumbnailStorage);
		Document document = documentMapper.requestToDocument(dto);
		document.setFileUrl(fileUrl);
		document.setThumbnailUrl(thumbnailUrl);
		User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Categorys not found"));
		document.setCategory(category);
		document.setUser(user);
		Document saved = documentRepository.save(document);
		DocumentResponse response = documentMapper.documentToResponse(saved);
		return response;
	}

	public File getDownloadFile(String fileName) throws Exception {
		if (fileName == null) {
			throw new NullPointerException("fileName is null");
		}
		var fileToDownload = new File(documentStorage + File.separator + fileName);
		if (!Objects.equals(fileToDownload.getParent(), documentStorage)) {
			throw new SecurityException("Unsupported filename!");
		}
		if (!fileToDownload.exists()) {
			throw new FileNotFoundException("No file named: " + fileName);
		}
		return fileToDownload;
	}
}

package com.example.app.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.DocumentRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.DocumentResponse;
import com.example.app.dto.response.FileResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.DocumentMapper;
import com.example.app.model.Category;
import com.example.app.model.Document;
import com.example.app.model.User;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.share.FileManager;
import com.example.app.share.GetUserByToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
	private final DocumentRepository documentRepository;
	private final CategoryRepository categoryRepository;
	private final DocumentMapper documentMapper;
	private final GetUserByToken getUserByToken;

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
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return documentMapper.documentToResponse(find);
	}

	public void increaseView(Long id) {
		documentRepository.findById(id).ifPresent(entity -> {
			entity.setViewsCount(entity.getViewsCount() + 1);
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

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			documentRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	public DocumentResponse hide(Long id, HideRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
		entity.setHide(dto.isHide());
		Document saved = documentRepository.save(entity);
		return documentMapper.documentToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public DocumentResponse changeStatus(Long id, DocumentRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		entity.setStatus(dto.getStatus());
		Document saved = documentRepository.save(entity);
		return documentMapper.documentToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public DocumentResponse update(Long id, DocumentRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		documentMapper.updateDocument(entity, dto);
		entity.setUpdatedAt(LocalDateTime.now());
		Document saved = documentRepository.save(entity);
		return documentMapper.documentToResponse(saved);
	}

	@PreAuthorize("hasAuthority('UPlOAD_FILE')")
	public DocumentResponse uploadFile(MultipartFile fileToSave, MultipartFile img, DocumentRequest dto)
			throws IOException {
		Document document = documentMapper.requestToDocument(dto);
		FileManager fileStorage = new FileManager();
		String fileUrl = fileStorage.saveFile(fileToSave, documentStorage);
		if (!fileUrl.endsWith(".pdf")) {
			int index = fileUrl.lastIndexOf(".");
			String result = (index != -1) ? fileUrl.substring(0, index) + ".pdf" : fileUrl;

			fileStorage.convertToPDF(documentStorage + "\\" + fileUrl, documentStorage + "\\" + result);
			fileStorage.deleteFile(documentStorage + "\\" + fileUrl);

			fileUrl = result;
		}
		document.setFileUrl(fileUrl);

		if (img.getOriginalFilename().endsWith(".png") || img.getOriginalFilename().endsWith(".jpg")) {
			String thumbnailUrl = fileStorage.saveFile(img, thumbnailStorage);
			document.setThumbnailUrl(thumbnailUrl);
		} else {
			throw new AppException("ảnh không đúng định dạng", 1001, HttpStatus.BAD_REQUEST);
		}

		document.setCreatedAt(LocalDateTime.now());

		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new AppException("category không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		User user = getUserByToken.get();
		document.setCategory(category);
		document.setUser(user);
		Document saved = documentRepository.save(document);
		DocumentResponse response = documentMapper.documentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('DOWNLOAD_FILE')")
	public File getDownloadFile(String fileName) throws Exception {
		if (fileName == null) {
			throw new AppException("file is null", 1001, HttpStatus.BAD_REQUEST);
		}
		File fileToDownload = new File(documentStorage + File.separator + fileName);
		if (!Objects.equals(fileToDownload.getParent(), documentStorage)) {
			throw new AppException("Unsupported filename!", 1001, HttpStatus.BAD_REQUEST);
		}
		if (!fileToDownload.exists()) {
			throw new AppException("No file named: " + fileName, 1001, HttpStatus.BAD_REQUEST);
		}
		return fileToDownload;
	}

	public FileResponse loadDocumentFile(Long id) throws IOException {

		Document doc = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

		String filePath = documentStorage + "\\" + doc.getFileUrl();

		File file = new File(filePath);

		if (!file.exists()) {
			throw new RuntimeException("File không tồn tại trong hệ thống");
		}

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		return new FileResponse(resource, file.length(), MediaType.APPLICATION_PDF);
	}

	// chưa biết làm gì với nó
	public void increaseDownload(Long id) {
		documentRepository.findById(id).ifPresent(entity -> {
			entity.setDownloadsCount(entity.getDownloadsCount() + 1);
			documentRepository.save(entity);
		});
	}

}

package com.example.app.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
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
import com.example.app.dto.response.DocumentFavoriteResponse;
import com.example.app.dto.response.DocumentResponse;
import com.example.app.dto.response.DocumentStatsResponse;
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
import com.example.app.share.SendNotification;
import com.example.app.share.Status;
import com.example.app.share.Type;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
	private final DocumentRepository documentRepository;
	private final CategoryRepository categoryRepository;
	private final DocumentMapper documentMapper;
	private final GetUserByToken getUserByToken;
	private final FileManager fileStorage;
	private final SendNotification sendNotification;

	@Value("${app.storage-directory-document}")
	private String documentStorage;

	@Value("${app.storage-directory-image}")
	private String thumbnailStorage;

	public DocumentStatsResponse getStats() {
		return documentRepository.getStats();
	}

	public DocumentResponse findByIdPublicDocument(Long id) {
		Document find = documentRepository.findByIdAndStatusAndHideFalse(id, Status.PUBLISHED)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return documentMapper.documentToResponse(find);
	}

	public void increaseView(Long id) {
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

	public FileResponse loadPublicDocumentFile(Long id) throws IOException {
		Document doc = documentRepository.findByIdAndStatusAndHideFalse(id, Status.PUBLISHED)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));
		return loadDocumentFile(doc);
	}

	public Long countDocumentOfUser(Long userId) {
		return documentRepository.countByUser_IdAndStatusAndHideFalse(userId, Status.PUBLISHED);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public DocumentResponse findById(Long id) {
		Document find = documentRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return documentMapper.documentToResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<DocumentResponse> getAllDocuments() {
		List<Document> documents = documentRepository.findAll();
		List<DocumentResponse> response = new ArrayList<DocumentResponse>();
		for (Document d : documents) {
			response.add(documentMapper.documentToResponse(d));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			Document entity = documentRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
			fileStorage.deleteFile(documentStorage + File.separator + entity.getFileUrl());
			fileStorage.deleteFile(thumbnailStorage + File.separator + entity.getThumbnailUrl());
			documentRepository.deleteById(id);

			User admin = getUserByToken.get();
			sendNotification.sendNotificationDelete(entity.getTitle(), entity.getUser().getId(), admin, Type.DOCUMENT);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	public DocumentResponse hide(Long id, HideRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
		boolean tempHide = entity.isHide();
		entity.setHide(dto.isHide());
		Document saved = documentRepository.save(entity);

		User admin = getUserByToken.get();
		sendNotification.sendNotificationHide(dto.isHide(), tempHide, entity.getTitle(), entity.getUser().getId(),
				admin, Type.DOCUMENT);
		return documentMapper.documentToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public DocumentResponse update(Long id, DocumentRequest dto) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		boolean tempHide = entity.isHide();
		Status tempStatus = entity.getStatus();
		documentMapper.updateDocument(entity, dto);
		entity.setUpdatedAt(LocalDateTime.now());
		Document saved = documentRepository.save(entity);

		User admin = getUserByToken.get();
		Long entityUserId = entity.getUser().getId();
		String entityTitle = entity.getTitle();
		sendNotification.sendNotificationPublished(dto.getStatus(), tempStatus, entity.getId(), entityTitle,
				entity.getUser().getUsername(), entityUserId, admin, Type.DOCUMENT);
		sendNotification.sendNotificationHide(dto.isHide(), tempHide, entityTitle, entityUserId, admin, Type.DOCUMENT);
		return documentMapper.documentToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public FileResponse loadAnyDocumentFile(Long id) throws IOException {
		Document doc = documentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));
		return loadDocumentFile(doc);
	}

	@PreAuthorize("hasAuthority('UPLOAD_FILE')")
	@Transactional
	public DocumentResponse uploadFile(MultipartFile fileToSave, DocumentRequest dto) throws IOException {
		Document document = documentMapper.requestToDocument(dto);
		document.setCreatedAt(LocalDateTime.now());

		String fileUrl = handlefile(fileToSave);
		document.setFileUrl(fileUrl);

		String thumbnailUrl = handleThumbnail(documentStorage + "\\" + fileUrl);
		document.setThumbnailUrl(thumbnailUrl);

		Category category = dto.getCategoryId() != null ? categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new AppException("category không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		document.setCategory(category);

		User user = getUserByToken.get();
		document.setUser(user);

		Document saved = documentRepository.save(document);
		DocumentResponse response = documentMapper.documentToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('DOWNLOAD_FILE')")
	public FileResponse downloadById(Long id) throws IOException {

		Document doc = documentRepository.findByIdAndStatusAndHideFalse(id, Status.PUBLISHED)
				.orElseThrow(() -> new AppException("Document không tồn tại", 1001, HttpStatus.NOT_FOUND));

		String storedFileName = doc.getFileUrl();
		File file = new File(documentStorage + File.separator + storedFileName);

		if (!file.exists()) {
			throw new AppException("File không tồn tại trong hệ thống", 1001, HttpStatus.NOT_FOUND);
		}

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		String downloadName = doc.getTitle() + ".pdf";

		return new FileResponse(resource, file.length(), MediaType.APPLICATION_PDF, downloadName);
	}

	public List<DocumentFavoriteResponse> search(String keyword, Long categoryId) {

		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.searchWithWithoutFavorite(keyword, categoryId);
		}

		return documentRepository.searchWithFavoriteStatus(keyword, categoryId, user.getId());
	}

	public List<DocumentFavoriteResponse> getAllPublicDocumentsCheckFavorite() {
		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.findAllWithoutFavorite();
		}
		return documentRepository.findAllWithFavoriteStatus(user.getId());

	}

	public List<DocumentFavoriteResponse> getDocumentsByUserCheckFavorite(Long authorId, Long currentDocumentId) {
		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.findDocumentsByUserWithoutFavorite(authorId, currentDocumentId);
		}
		return documentRepository.findDocumentsByUserWithFavoriteStatus(authorId, user.getId(), currentDocumentId);

	}

	public List<DocumentFavoriteResponse> getDocumentsByCategoryCheckFavorite(Long categoryId, Long currentDocumentId) {
		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.findDocumentsByCategoryWithoutFavorite(categoryId, currentDocumentId);
		}
		return documentRepository.findDocumentsByCategoryWithFavoriteStatus(categoryId, user.getId(),
				currentDocumentId);

	}

	public List<DocumentFavoriteResponse> getAllDocumentsByUserheckFavorite(Long authorId) {
		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.findAllDocumentsByUserWithoutFavorite(authorId);
		}
		return documentRepository.findAllDocumentsByUserWithFavoriteStatus(authorId, user.getId());
	}

	//

	@PreAuthorize("hasAuthority('GET_MY_DOCUMENT')")
	public List<DocumentResponse> getMyDocument() {
		User user = getUserByToken.get();
		List<Document> documents = documentRepository.findByUser_Id(user.getId());
		List<DocumentResponse> response = new ArrayList<DocumentResponse>();
		for (Document d : documents) {
			response.add(documentMapper.documentToResponse(d));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('UPDATE_MY_DOCUMENT')")
	public DocumentResponse updateMyDocument(Long id, DocumentRequest dto) {
		User user = getUserByToken.get();
		Document entity = documentRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		boolean tempHide = entity.isHide();
		documentMapper.updateDocument(entity, dto);
		entity.setUpdatedAt(LocalDateTime.now());
		Document saved = documentRepository.save(entity);
		sendNotification.sendNotificationMyHide(dto.isHide(), tempHide, entity.getTitle(), user.getId(),
				user.getUsername(), Type.DOCUMENT);
		return documentMapper.documentToResponse(saved);
	}

	@PreAuthorize("hasAuthority('DELETE_MY_DOCUMENT')")
	public void deleteMyDocument(Long id) {
		try {
			User user = getUserByToken.get();
			Document entity = documentRepository.findByIdAndUser_Id(id, user.getId())
					.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
			fileStorage.deleteFile(documentStorage + File.separator + entity.getFileUrl());
			fileStorage.deleteFile(thumbnailStorage + File.separator + entity.getThumbnailUrl());
			documentRepository.deleteById(id);
			sendNotification.sendNotificationMyDelete(entity.getTitle(), user.getId(), user.getUsername(),
					Type.DOCUMENT);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('COUNT_MY_DOCUMENT')")
	public Long countMyDocument() {
		User user = getUserByToken.get();
		return documentRepository.countByUser_Id(user.getId());
	}

	private FileResponse loadDocumentFile(Document doc) throws IOException {

		String filePath = documentStorage + "\\" + doc.getFileUrl();

		File file = new File(filePath);

		if (!file.exists()) {
			throw new RuntimeException("File không tồn tại trong hệ thống");
		}

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		return new FileResponse(resource, file.length(), MediaType.APPLICATION_PDF, doc.getTitle());
	}

	private String handlefile(MultipartFile fileToSave) throws IOException {
		String fileName = fileToSave.getOriginalFilename();
		if (fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx")
				|| fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {

			String fileUrl = fileStorage.saveFile(fileToSave, documentStorage);
			if (!fileUrl.endsWith(".pdf")) {
				int index = fileUrl.lastIndexOf(".");
				String result = (index != -1) ? fileUrl.substring(0, index) + ".pdf" : fileUrl;

				String input = documentStorage + File.separator + fileUrl;
				String output = documentStorage + File.separator + result;

				fileStorage.convertToPDF(input, output);

				fileUrl = result;
			}
			return fileUrl;
		} else {
			throw new AppException("file không hợp lệ", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	private String handleThumbnail(String url) {
		File docFile = new File(url);
		// Load pdf
		try (PDDocument doc = PDDocument.load(docFile)) {
			// Render trang đầu
			PDFRenderer renderer = new PDFRenderer(doc);
			BufferedImage image = renderer.renderImageWithDPI(0, 150); // 0 = trang đầu

			// Lưu ra ảnh PNG
			String thumbnailUrl = UUID.randomUUID().toString() + ".png";
			String outputPath = thumbnailStorage + File.separator + thumbnailUrl;
			ImageIO.write(image, "png", new File(outputPath));
			return thumbnailUrl;
		} catch (Exception e) {
			throw new AppException(e.getMessage(), 1001, HttpStatus.BAD_REQUEST);
		}
	}

}

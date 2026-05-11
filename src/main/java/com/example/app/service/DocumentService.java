package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.constant.ContentStatus;
import com.example.app.constant.NotificationAction;
import com.example.app.dto.request.DocumentRequest;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.document.DocumentAdminResponse;
import com.example.app.dto.response.document.DocumentDetailResponse;
import com.example.app.dto.response.document.DocumentEventDTO;
import com.example.app.dto.response.document.DocumentResponse;
import com.example.app.dto.response.document.DocumentStatsResponse;
import com.example.app.dto.response.document.DocumentUserResponse;
import com.example.app.event.DocumentDeleteEvent;
import com.example.app.event.DocumentStatusEvent;
import com.example.app.exception.AppException;
import com.example.app.helper.FileManager;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.DocumentMapper;
import com.example.app.model.Category;
import com.example.app.model.Document;
import com.example.app.model.User;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.DocumentCommentRepository;
import com.example.app.repository.DocumentFavoriteRepository;
import com.example.app.repository.DocumentRatingRepository;
import com.example.app.repository.DocumentReportRepository;
import com.example.app.repository.DocumentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {
	private final DocumentRepository documentRepository;
	private final CategoryRepository categoryRepository;
	private final DocumentFavoriteRepository favoriteDocumentRepository;
	private final DocumentRatingRepository ratingDocumentRepository;
	private final DocumentReportRepository reportDocumentRepository;
	private final DocumentCommentRepository commentDocumentRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final DocumentMapper documentMapper;
	private final GetUserByToken getUserByToken;
	private final FileManager fileStorage;

	@PreAuthorize("hasRole('ADMIN')")
	public DocumentDetailResponse findById(Long id) {
		Document find = documentRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return documentMapper.documentToDocumentDetailResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<DocumentAdminResponse> findAll() {
		List<Document> documents = documentRepository.findAll();
		List<DocumentAdminResponse> response = documents.stream().map(documentMapper::documentToDocumentAdminResponse)
				.toList();
		return response;
	}

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		eventPublisher.publishEvent(new DocumentDeleteEvent(entity));

		DocumentEventDTO dto = documentMapper.documentToDocumentDTO(entity);
		deleteByKey(id);

		User admin = getUserByToken.get();
		eventPublisher.publishEvent(new DocumentStatusEvent(dto, admin, NotificationAction.ADMIN_DELETE));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public DocumentDetailResponse update(Long id, DocumentRequest request) {
		Document entity = documentRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		ContentStatus initialStatus = entity.getStatus();

		documentMapper.updateDocument(entity, request);
		entity.setUpdatedAt(LocalDateTime.now());
		Document saved = documentRepository.save(entity);

		DocumentEventDTO dto = documentMapper.documentToDocumentDTO(saved);
		User admin = getUserByToken.get();
		if (initialStatus == ContentStatus.PENDING && saved.getStatus() == ContentStatus.PUBLISHED) {
			eventPublisher.publishEvent(new DocumentStatusEvent(dto, admin, NotificationAction.PUBLIC));
		}
		if (initialStatus == ContentStatus.PUBLISHED && saved.getStatus() == ContentStatus.HIDDEN) {
			eventPublisher.publishEvent(new DocumentStatusEvent(dto, admin, NotificationAction.ADMIN_HIDDEN));
		}

		return documentMapper.documentToDocumentDetailResponse(saved);
	}

	@PreAuthorize("hasAuthority('GET_MY_DOCUMENT')")
	public List<DocumentUserResponse> getMyDocument() {
		User user = getUserByToken.get();
		List<Document> documents = documentRepository.findByUser_Id(user.getId());
		List<DocumentUserResponse> response = documents.stream().map(documentMapper::documentToDocumentUserResponse)
				.toList();
		return response;
	}

	@PreAuthorize("hasAuthority('GET_MY_DOCUMENT_DETAIL')")
	public DocumentDetailResponse getMyDocumentDetail(Long id) {
		User user = getUserByToken.get();
		Document entity = documentRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return documentMapper.documentToDocumentDetailResponse(entity);
	}

	@PreAuthorize("hasAuthority('UPDATE_MY_DOCUMENT')")
	public DocumentUserResponse updateMyDocument(Long id, DocumentRequest request) {
		User user = getUserByToken.get();
		Document entity = documentRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		boolean initialState = entity.isHide();
		documentMapper.updateDocument(entity, request);
		entity.setUpdatedAt(LocalDateTime.now());
		Document saved = documentRepository.save(entity);
		DocumentEventDTO dto = documentMapper.documentToDocumentDTO(saved);

		if (initialState == false && saved.isHide() == true && saved.getStatus() == ContentStatus.PUBLISHED) {
			eventPublisher.publishEvent(new DocumentStatusEvent(dto, user, NotificationAction.AUTHOR_HIDDEN));
		}
		return documentMapper.documentToDocumentUserResponse(saved);
	}

	@PreAuthorize("hasAuthority('DELETE_MY_DOCUMENT')")
	public void deleteMyDocument(Long id) {
		User user = getUserByToken.get();
		Document entity = documentRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		eventPublisher.publishEvent(new DocumentDeleteEvent(entity));

		DocumentEventDTO dto = documentMapper.documentToDocumentDTO(entity);
		deleteByKey(id);
		if (dto.getStatus() == ContentStatus.PUBLISHED) {
			eventPublisher.publishEvent(new DocumentStatusEvent(dto, user, NotificationAction.AUTHOR_DELETE));
		}
	}

	@PreAuthorize("hasAuthority('COUNT_MY_DOCUMENT')")
	public Long countMyDocument() {
		User user = getUserByToken.get();
		return documentRepository.countByUser_Id(user.getId());
	}

	@PreAuthorize("hasAuthority('UPLOAD_FILE')")
	@Transactional
	public DocumentDetailResponse uploadFile(MultipartFile fileToSave, DocumentRequest dto) {
		Document document = documentMapper.requestToDocument(dto);
		document.setCreatedAt(LocalDateTime.now());

		try {
			Map<?, ?> handleDoc = fileStorage.uploadPdf(fileToSave);

			String url = (String) handleDoc.get("secure_url");
			String publicId = (String) handleDoc.get("public_id");
			document.setFileUrl(url);

			String thumbnailUrl = fileStorage.getThumbnail(publicId, "pdf");
			document.setThumbnailUrl(thumbnailUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Category category = dto.getCategoryId() != null ? categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new AppException("category không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		document.setCategory(category);

		User user = getUserByToken.get();
		document.setUser(user);

		Document saved = documentRepository.save(document);
		DocumentDetailResponse response = documentMapper.documentToDocumentDetailResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('DOWNLOAD_FILE')")
	public FileResponse downloadById(Long id) throws Exception {

		Document doc = documentRepository.findByIdAndStatusAndHideFalse(id, ContentStatus.PUBLISHED)
				.orElseThrow(() -> new AppException("Document không tồn tại", 1001, HttpStatus.NOT_FOUND));

		FileResponse file = fileStorage.downloadFile(doc.getFileUrl());
		return file;

	}

	public List<DocumentResponse> search(String keyword, Long categoryId) {

		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.searchWithoutLogin(keyword, categoryId, ContentStatus.PUBLISHED);
		}

		return documentRepository.searchWhenLogin(keyword, categoryId, user.getId(), ContentStatus.PUBLISHED);
	}

	public List<DocumentResponse> getAllPublicDocuments() {
		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.getAllWithoutLogin(ContentStatus.PUBLISHED);
		}
		return documentRepository.getAllWhenLogin(user.getId(), ContentStatus.PUBLISHED);

	}

	public List<DocumentResponse> getDocumentsByUser(Long authorId, Long currentDocumentId) {
		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.getByUserWithoutLoginAndDifferentCurrentDocument(authorId, currentDocumentId,
					ContentStatus.PUBLISHED);
		}
		return documentRepository.getByUserWhenLoginAndDifferentCurrentDocument(authorId, user.getId(),
				currentDocumentId, ContentStatus.PUBLISHED);

	}

	public List<DocumentResponse> getDocumentsByCategory(Long categoryId, Long currentDocumentId) {
		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.getByCategoryWithoutLoginAndDifferentCurrentDocument(categoryId,
					currentDocumentId, ContentStatus.PUBLISHED);
		}
		return documentRepository.getByCategoryWhenLoginAndDifferentCurrentDocument(categoryId, user.getId(),
				currentDocumentId, ContentStatus.PUBLISHED);

	}

	public List<DocumentResponse> getAllDocumentsByUser(Long authorId) {
		User user = getUserByToken.get();
		if (user == null) {
			return documentRepository.getByUserWithoutLogin(authorId, ContentStatus.PUBLISHED);
		}
		return documentRepository.getByUserWhenLogin(authorId, user.getId(), ContentStatus.PUBLISHED);
	}

	public DocumentStatsResponse getStats() {
		return documentRepository.getStats(ContentStatus.PUBLISHED);
	}

	public DocumentDetailResponse findByIdPublicDocument(Long id) {
		Document find = documentRepository.findByIdAndStatusAndHideFalse(id, ContentStatus.PUBLISHED)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return documentMapper.documentToDocumentDetailResponse(find);
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

	public Long countDocumentOfUser(Long userId) {
		return documentRepository.countByUser_IdAndStatusAndHideFalse(userId, ContentStatus.PUBLISHED);
	}

	private void deleteByKey(Long id) {
		favoriteDocumentRepository.deleteByDocument_Id(id);
		ratingDocumentRepository.deleteByDocument_Id(id);
		reportDocumentRepository.deleteByDocument_Id(id);
		commentDocumentRepository.deleteByDocument_Id(id);
		documentRepository.deleteById(id);
	}
}

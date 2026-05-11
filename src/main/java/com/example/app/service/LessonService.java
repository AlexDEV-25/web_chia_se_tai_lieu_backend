package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.constant.ContentStatus;
import com.example.app.constant.NotificationAction;
import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.lesson.LessonAdminResponse;
import com.example.app.dto.response.lesson.LessonDTO;
import com.example.app.dto.response.lesson.LessonDetailResponse;
import com.example.app.dto.response.lesson.LessonResponse;
import com.example.app.dto.response.lesson.LessonStatsResponse;
import com.example.app.dto.response.lesson.LessonUserResponse;
import com.example.app.event.LessonDeleteEvent;
import com.example.app.event.LessonStatusEvent;
import com.example.app.exception.AppException;
import com.example.app.helper.FileManager;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.LessonMapper;
import com.example.app.model.Category;
import com.example.app.model.Lesson;
import com.example.app.model.User;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.LessonCommentRepository;
import com.example.app.repository.LessonFavoriteRepository;
import com.example.app.repository.LessonRatingRepository;
import com.example.app.repository.LessonReportRepository;
import com.example.app.repository.LessonRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LessonService {
	private final LessonRepository lessonRepository;
	private final CategoryRepository categoryRepository;
	private final LessonFavoriteRepository favoriteLessonRepository;
	private final LessonRatingRepository ratingLessonRepository;
	private final LessonReportRepository reportLessonRepository;
	private final LessonCommentRepository commentLessonRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final LessonMapper lessonMapper;
	private final GetUserByToken getUserByToken;
	private final FileManager fileStorage;

	@PreAuthorize("hasRole('ADMIN')")
	public LessonDetailResponse findById(Long id) {
		Lesson find = lessonRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return lessonMapper.lessonToLessonDetailResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<LessonAdminResponse> getAllLessons() {
		List<Lesson> lessons = lessonRepository.findAll();
		List<LessonAdminResponse> response = lessons.stream().map(lessonMapper::lessonToLessonAdminResponse).toList();
		return response;
	}

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		Lesson entity = lessonRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy lesson"));

		eventPublisher.publishEvent(new LessonDeleteEvent(entity));

		LessonDTO dto = lessonMapper.lessonToLessonDTO(entity);
		deleteByKey(id);

		User admin = getUserByToken.get();
		eventPublisher.publishEvent(new LessonStatusEvent(dto, admin, NotificationAction.ADMIN_DELETE));
	}

	@PreAuthorize("hasRole('ADMIN')")
	public LessonDetailResponse update(Long id, LessonRequest request) {
		Lesson entity = lessonRepository.findById(id)
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		ContentStatus initialStatus = entity.getStatus();

		lessonMapper.updateLesson(entity, request);
		entity.setUpdatedAt(LocalDateTime.now());
		Lesson saved = lessonRepository.save(entity);

		LessonDTO dto = lessonMapper.lessonToLessonDTO(saved);

		User admin = getUserByToken.get();
		if (initialStatus == ContentStatus.PENDING && saved.getStatus() == ContentStatus.PUBLISHED) {
			eventPublisher.publishEvent(new LessonStatusEvent(dto, admin, NotificationAction.PUBLIC));
		}
		if (initialStatus == ContentStatus.PUBLISHED && saved.getStatus() == ContentStatus.HIDDEN) {
			eventPublisher.publishEvent(new LessonStatusEvent(dto, admin, NotificationAction.ADMIN_HIDDEN));
		}
		return lessonMapper.lessonToLessonDetailResponse(saved);
	}

	@PreAuthorize("hasAuthority('GET_MY_LESSON')")
	public List<LessonUserResponse> getMyLesson() {
		User user = getUserByToken.get();
		List<Lesson> lessons = lessonRepository.findByUser_Id(user.getId());
		List<LessonUserResponse> response = lessons.stream().map(lessonMapper::lessonToLessonUserResponse).toList();
		return response;
	}

	@PreAuthorize("hasAuthority('GET_MY_LESSON_DETAIL')")
	public LessonDetailResponse getMyLessonDetail(Long id) {
		User user = getUserByToken.get();
		Lesson entity = lessonRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return lessonMapper.lessonToLessonDetailResponse(entity);
	}

	@PreAuthorize("hasAuthority('UPDATE_MY_LESSON')")
	public LessonUserResponse updateMyDocument(Long id, LessonRequest request) {
		User user = getUserByToken.get();
		Lesson entity = lessonRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		boolean initialState = entity.isHide();
		lessonMapper.updateLesson(entity, request);
		entity.setUpdatedAt(LocalDateTime.now());
		Lesson saved = lessonRepository.save(entity);
		LessonDTO dto = lessonMapper.lessonToLessonDTO(saved);

		if (initialState == false && saved.isHide() == true && saved.getStatus() == ContentStatus.PUBLISHED) {
			eventPublisher.publishEvent(new LessonStatusEvent(dto, user, NotificationAction.AUTHOR_HIDDEN));
		}
		return lessonMapper.lessonToLessonUserResponse(saved);
	}

	@PreAuthorize("hasAuthority('DELETE_MY_LESSON')")
	public void deleteMyLesson(Long id) {
		try {
			User user = getUserByToken.get();
			Lesson entity = lessonRepository.findByIdAndUser_Id(id, user.getId())
					.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));

			eventPublisher.publishEvent(new LessonDeleteEvent(entity));

			LessonDTO dto = lessonMapper.lessonToLessonDTO(entity);
			deleteByKey(id);

			if (dto.getStatus() == ContentStatus.PUBLISHED) {
				eventPublisher.publishEvent(new LessonStatusEvent(dto, user, NotificationAction.AUTHOR_DELETE));
			}
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('COUNT_MY_LESSON')")
	public Long countMyLesson() {
		User user = getUserByToken.get();
		return lessonRepository.countByUser_Id(user.getId());
	}

	@PreAuthorize("hasAuthority('UPLOAD_LESSON')")
	@Transactional
	public LessonDetailResponse uploadLesson(MultipartFile video, MultipartFile document, MultipartFile subFile,
			LessonRequest dto) {
		Lesson lesson = lessonMapper.requestToLesson(dto);
		lesson.setCreatedAt(LocalDateTime.now());

		try {
			if (video != null) {
				Map<?, ?> handleVideo = fileStorage.uploadVideo(video);

				String lessonUrl = (String) handleVideo.get("secure_url");
				String publicId = (String) handleVideo.get("public_id");
				lesson.setLessonUrl(lessonUrl);

				String thumbnailUrl = fileStorage.getThumbnail(publicId, "video");
				lesson.setThumbnailUrl(thumbnailUrl);
			}

			if (document != null) {
				Map<?, ?> handleDoc = fileStorage.uploadPdf(document);

				String documentUrl = (String) handleDoc.get("secure_url");
				lesson.setDocumentUrl(documentUrl);
			}

			if (subFile != null) {
				Map<?, ?> handleSubFile = fileStorage.uploadArchive(subFile);

				String subFileUrl = (String) handleSubFile.get("secure_url");
				lesson.setSubFileUrl(subFileUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Category category = dto.getCategoryId() != null ? categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new AppException("category không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		lesson.setCategory(category);

		User user = getUserByToken.get();
		lesson.setUser(user);

		Lesson saved = lessonRepository.save(lesson);
		LessonDetailResponse response = lessonMapper.lessonToLessonDetailResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('DOWNLOAD_LESSON_DOCUMENT')")
	public FileResponse downloadDocumentByLessonId(Long lessonId) throws Exception {

		Lesson lesson = lessonRepository.findByIdAndStatusAndHideFalse(lessonId, ContentStatus.PUBLISHED)
				.orElseThrow(() -> new AppException("Lesson không tồn tại", 1001, HttpStatus.NOT_FOUND));

		if (lesson.getDocumentUrl() == null) {
			throw new AppException("Lesson không có tài liệu", 1001, HttpStatus.BAD_REQUEST);
		}

		FileResponse file = fileStorage.downloadFile(lesson.getDocumentUrl());
		return file;
	}

	@PreAuthorize("hasAuthority('DOWNLOAD_LESSON_SUBFILE')")
	public FileResponse downloadSubFileByLessonId(Long lessonId) throws Exception {

		Lesson lesson = lessonRepository.findByIdAndStatusAndHideFalse(lessonId, ContentStatus.PUBLISHED)
				.orElseThrow(() -> new AppException("Lesson không tồn tại", 1001, HttpStatus.NOT_FOUND));

		if (lesson.getSubFileUrl() == null) {
			throw new AppException("Lesson không có file đính kèm", 1001, HttpStatus.BAD_REQUEST);
		}

		FileResponse file = fileStorage.downloadFile(lesson.getSubFileUrl());
		return file;
	}

	public List<LessonResponse> search(String keyword, Long categoryId) {

		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.searchWithoutLogin(keyword, categoryId, ContentStatus.PUBLISHED);
		}
		return lessonRepository.searchWhenLogin(keyword, categoryId, user.getId(), ContentStatus.PUBLISHED);
	}

	public List<LessonResponse> getAllPublicLessonsCheckFavorite() {
		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.getAllWithoutLogin(ContentStatus.PUBLISHED);
		}
		return lessonRepository.getAllWhenLogin(user.getId(), ContentStatus.PUBLISHED);
	}

	public List<LessonResponse> getLessonsByUserCheckFavorite(Long authorId, Long currentLessonId) {
		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.getByUserWithoutLoginAndDifferentCurrentLesson(authorId, currentLessonId,
					ContentStatus.PUBLISHED);
		}
		return lessonRepository.getByUserWhenLoginAndDifferentCurrentLesson(authorId, user.getId(), currentLessonId,
				ContentStatus.PUBLISHED);
	}

	public List<LessonResponse> getLessonsByCategoryCheckFavorite(Long categoryId, Long currentLessonId) {
		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.getByCategoryWithoutLoginAndDifferentCurrentLesson(categoryId, currentLessonId,
					ContentStatus.PUBLISHED);
		}
		return lessonRepository.getByCategoryWhenLoginAndDifferentCurrentLesson(categoryId, user.getId(),
				currentLessonId, ContentStatus.PUBLISHED);
	}

	public List<LessonResponse> getAllLessonsByUserCheckFavorite(Long authorId) {
		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.getByUserWithoutLogin(authorId, ContentStatus.PUBLISHED);
		}
		return lessonRepository.getByUserWhenLogin(authorId, user.getId(), ContentStatus.PUBLISHED);
	}

	public LessonStatsResponse getStats() {
		return lessonRepository.getStats(ContentStatus.PUBLISHED);
	}

	public LessonDetailResponse findByIdPublicLesson(Long id) {
		Lesson find = lessonRepository.findByIdAndStatusAndHideFalse(id, ContentStatus.PUBLISHED)
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return lessonMapper.lessonToLessonDetailResponse(find);
	}

	public void increaseView(Long id) {
		lessonRepository.findById(id).ifPresent(entity -> {
			entity.setViewsCount(entity.getViewsCount() + 1);
			lessonRepository.save(entity);
		});
	}

	public Long countLessonOfUser(Long userId) {
		return lessonRepository.countByUser_IdAndStatusAndHideFalse(userId, ContentStatus.PUBLISHED);
	}

	private void deleteByKey(Long id) {
		favoriteLessonRepository.deleteByLesson_Id(id);
		ratingLessonRepository.deleteByLesson_Id(id);
		reportLessonRepository.deleteByLesson_Id(id);
		commentLessonRepository.deleteByLesson_Id(id);
		lessonRepository.deleteById(id);
	}
}

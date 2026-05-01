package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.HideRequest;
import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.lesson.LessonAdminResponse;
import com.example.app.dto.response.lesson.LessonDetailResponse;
import com.example.app.dto.response.lesson.LessonFavoriteResponse;
import com.example.app.dto.response.lesson.LessonStatsResponse;
import com.example.app.dto.response.lesson.LessonUserResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.LessonMapper;
import com.example.app.model.Category;
import com.example.app.model.Lesson;
import com.example.app.model.User;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.FavoriteRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.repository.RatingRepository;
import com.example.app.repository.ReportRepository;
import com.example.app.share.FileManager;
import com.example.app.share.GetUserByToken;
import com.example.app.share.SendNotification;
import com.example.app.share.Status;
import com.example.app.share.Type;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LessonService {
	private final LessonRepository lessonRepository;
	private final CategoryRepository categoryRepository;
	private final FavoriteRepository favoriteRepository;
	private final RatingRepository ratingRepository;
	private final ReportRepository reportRepository;
	private final CommentRepository commentRepository;
	private final LessonMapper lessonMapper;
	private final GetUserByToken getUserByToken;
	private final FileManager fileStorage;
	private final SendNotification sendNotification;

	@PreAuthorize("hasRole('ADMIN')")
	public LessonDetailResponse findById(Long id) {
		Lesson find = lessonRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return lessonMapper.lessonToLessonDetailResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<LessonAdminResponse> getAllLessons() {
		List<Lesson> lessons = lessonRepository.findAll();
		List<LessonAdminResponse> response = new ArrayList<LessonAdminResponse>();
		for (Lesson l : lessons) {
			response.add(lessonMapper.lessonToLessonAdminResponse(l));
		}
		return response;
	}

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			Lesson entity = lessonRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy lesson"));

			try {
				fileStorage.deleteFile(entity.getLessonUrl(), "video");
				fileStorage.deleteFile(entity.getDocumentUrl(), "image");
				fileStorage.deleteFile(entity.getThumbnailUrl(), "image");
				fileStorage.deleteFile(entity.getSubFileUrl(), "raw");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			deleteByKey(id);

			User admin = getUserByToken.get();
			sendNotification.sendNotificationDelete(entity.getTitle(), entity.getUser().getId(), admin, Type.LESSON);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	public LessonDetailResponse hide(Long id, HideRequest dto) {
		Lesson entity = lessonRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy lesson"));
		boolean tempHide = entity.isHide();
		entity.setHide(dto.isHide());
		Lesson saved = lessonRepository.save(entity);

		User admin = getUserByToken.get();
		sendNotification.sendNotificationHide(dto.isHide(), tempHide, entity.getTitle(), entity.getUser().getId(),
				admin, Type.LESSON);
		return lessonMapper.lessonToLessonDetailResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public LessonDetailResponse update(Long id, LessonRequest dto) {
		Lesson entity = lessonRepository.findById(id)
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		boolean tempHide = entity.isHide();
		Status tempStatus = entity.getStatus();
		lessonMapper.updateLesson(entity, dto);
		entity.setUpdatedAt(LocalDateTime.now());
		Lesson saved = lessonRepository.save(entity);

		User admin = getUserByToken.get();
		Long entityUserId = entity.getUser().getId();
		String entityTitle = entity.getTitle();
		sendNotification.sendNotificationPublished(dto.getStatus(), tempStatus, entity.getId(), entityTitle,
				entity.getUser().getUsername(), entityUserId, admin, Type.LESSON);
		sendNotification.sendNotificationHide(dto.isHide(), tempHide, entityTitle, entityUserId, admin, Type.LESSON);
		return lessonMapper.lessonToLessonDetailResponse(saved);
	}

	@PreAuthorize("hasAuthority('GET_MY_LESSON')")
	public List<LessonUserResponse> getMyLesson() {
		User user = getUserByToken.get();
		List<Lesson> lessons = lessonRepository.findByUser_Id(user.getId());
		List<LessonUserResponse> response = new ArrayList<LessonUserResponse>();
		for (Lesson l : lessons) {
			response.add(lessonMapper.lessonToLessonUserResponse(l));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('UPDATE_MY_LESSON')")
	public LessonUserResponse updateMyDocument(Long id, LessonRequest dto) {
		User user = getUserByToken.get();
		Lesson entity = lessonRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		boolean tempHide = entity.isHide();
		lessonMapper.updateLesson(entity, dto);
		entity.setUpdatedAt(LocalDateTime.now());
		Lesson saved = lessonRepository.save(entity);
		sendNotification.sendNotificationMyHide(dto.isHide(), tempHide, entity.getTitle(), user.getId(),
				user.getUsername(), Type.LESSON);
		return lessonMapper.lessonToLessonUserResponse(saved);
	}

	@PreAuthorize("hasAuthority('DELETE_MY_LESSON')")
	public void deleteMyLesson(Long id) {
		try {
			User user = getUserByToken.get();
			Lesson entity = lessonRepository.findByIdAndUser_Id(id, user.getId())
					.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
			try {
				fileStorage.deleteFile(entity.getLessonUrl(), "video");
				fileStorage.deleteFile(entity.getThumbnailUrl(), "image");
				fileStorage.deleteFile(entity.getDocumentUrl(), "image");
				fileStorage.deleteFile(entity.getSubFileUrl(), "raw");
			} catch (Exception e) {
				e.printStackTrace();
			}

			deleteByKey(id);

			sendNotification.sendNotificationMyDelete(entity.getTitle(), user.getId(), user.getUsername(), Type.LESSON);
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
				String subFileName = fileStorage.fileName(subFile);
				Map<?, ?> handleSubFile = fileStorage.uploadArchive(subFile, subFileName);

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

		Lesson lesson = lessonRepository.findByIdAndStatusAndHideFalse(lessonId, Status.PUBLISHED)
				.orElseThrow(() -> new AppException("Lesson không tồn tại", 1001, HttpStatus.NOT_FOUND));

		if (lesson.getDocumentUrl() == null) {
			throw new AppException("Lesson không có tài liệu", 1001, HttpStatus.BAD_REQUEST);
		}

		FileResponse file = fileStorage.downloadFile(lesson.getDocumentUrl());
		return file;
	}

	@PreAuthorize("hasAuthority('DOWNLOAD_LESSON_SUBFILE')")
	public FileResponse downloadSubFileByLessonId(Long lessonId) throws Exception {

		Lesson lesson = lessonRepository.findByIdAndStatusAndHideFalse(lessonId, Status.PUBLISHED)
				.orElseThrow(() -> new AppException("Lesson không tồn tại", 1001, HttpStatus.NOT_FOUND));

		if (lesson.getSubFileUrl() == null) {
			throw new AppException("Lesson không có file đính kèm", 1001, HttpStatus.BAD_REQUEST);
		}

		FileResponse file = fileStorage.downloadFile(lesson.getSubFileUrl());
		return file;
	}

	public List<LessonFavoriteResponse> search(String keyword, Long categoryId) {

		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.searchWithWithoutFavorite(keyword, categoryId);
		}
		return lessonRepository.searchWithFavoriteStatus(keyword, categoryId, user.getId());
	}

	public List<LessonFavoriteResponse> getAllPublicLessonsCheckFavorite() {
		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.findAllWithoutFavorite();
		}
		return lessonRepository.findAllWithFavoriteStatus(user.getId());
	}

	public List<LessonFavoriteResponse> getLessonsByUserCheckFavorite(Long authorId, Long currentLessonId) {
		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.findLessonsByUserWithoutFavorite(authorId, currentLessonId);
		}
		return lessonRepository.findLessonsByUserWithFavoriteStatus(authorId, user.getId(), currentLessonId);
	}

	public List<LessonFavoriteResponse> getLessonsByCategoryCheckFavorite(Long categoryId, Long currentLessonId) {
		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.findLessonsByCategoryWithoutFavorite(categoryId, currentLessonId);
		}
		return lessonRepository.findLessonsByCategoryWithFavoriteStatus(categoryId, user.getId(), currentLessonId);
	}

	public List<LessonFavoriteResponse> getAllLessonsByUserCheckFavorite(Long authorId) {
		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.findAllLessonsByUserWithoutFavorite(authorId);
		}
		return lessonRepository.findAllLessonsByUserWithFavoriteStatus(authorId, user.getId());
	}

	public LessonStatsResponse getStats() {
		return lessonRepository.getStats();
	}

	public LessonDetailResponse findByIdPublicLesson(Long id) {
		Lesson find = lessonRepository.findByIdAndStatusAndHideFalse(id, Status.PUBLISHED)
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
		return lessonRepository.countByUser_IdAndStatusAndHideFalse(userId, Status.PUBLISHED);
	}

	private void deleteByKey(Long id) {
		favoriteRepository.deleteByLesson_Id(id);
		ratingRepository.deleteByLesson_Id(id);
		reportRepository.deleteByLesson_Id(id);
		commentRepository.deleteByLesson_Id(id);
		lessonRepository.deleteById(id);
	}

}

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

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.HideRequest;
import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.ContentRatingSummaryResponse;
import com.example.app.dto.response.ContentReportSummaryResponse;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.LessonFavoriteResponse;
import com.example.app.dto.response.LessonResponse;
import com.example.app.dto.response.LessonStatsResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.LessonMapper;
import com.example.app.model.Category;
import com.example.app.model.Lesson;
import com.example.app.model.User;
import com.example.app.repository.CategoryRepository;
import com.example.app.repository.LessonRepository;
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
	private final LessonMapper lessonMapper;
	private final GetUserByToken getUserByToken;
	private final FileManager fileStorage;
	private final SendNotification sendNotification;

	@Value("${app.storage-directory-document}")
	private String documentStorage;

	@Value("${app.storage-directory-image}")
	private String thumbnailStorage;

	@Value("${app.storage-directory-video}")
	private String videoStorage;

	@Value("${app.storage-directory-subfile}")
	private String subfileStorage;

	public LessonStatsResponse getStats() {
		return lessonRepository.getStats();
	}

	public List<LessonResponse> getAllPublicLessons() {
		List<Lesson> lessons = lessonRepository.findByStatusAndHideFalse(Status.PUBLISHED);
		List<LessonResponse> response = new ArrayList<LessonResponse>();
		for (Lesson d : lessons) {
			response.add(lessonMapper.lessonToResponse(d));
		}
		return response;
	}

	public LessonResponse findByIdPublicLesson(Long id) {
		Lesson find = lessonRepository.findByIdAndStatusAndHideFalse(id, Status.PUBLISHED)
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return lessonMapper.lessonToResponse(find);
	}

	public void increaseView(Long id) {
		lessonRepository.findById(id).ifPresent(entity -> {
			entity.setViewsCount(entity.getViewsCount() + 1);
			lessonRepository.save(entity);
		});
	}

	public FileResponse loadPublicDocumentFile(Long id) throws IOException {
		Lesson doc = lessonRepository.findByIdAndStatusAndHideFalse(id, Status.PUBLISHED)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));
		return loadDocumentFile(doc);
	}

	public File loadPublicLessonFile(Long id) throws IOException {
		Lesson lesson = lessonRepository.findByIdAndStatusAndHideFalse(id, Status.PUBLISHED)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy bài giảng"));
		return loadVideoFile(lesson);
	}

	public Long countLessonOfUser(Long userId) {
		return lessonRepository.countByUser_IdAndStatusAndHideFalse(userId, Status.PUBLISHED);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public LessonResponse findById(Long id) {
		Lesson find = lessonRepository.findById(id)
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		return lessonMapper.lessonToResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<LessonResponse> getAllLessons() {
		List<Lesson> lessons = lessonRepository.findAll();
		List<LessonResponse> response = new ArrayList<LessonResponse>();
		for (Lesson l : lessons) {
			response.add(lessonMapper.lessonToResponse(l));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ContentRatingSummaryResponse> getAllLessonRatingSummary() {
		List<ContentRatingSummaryResponse> response = lessonRepository.getAllLessonRatingSummary();
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<ContentReportSummaryResponse> getAllLessonReportSummary() {
		List<ContentReportSummaryResponse> response = lessonRepository.getAllLessonReportSummary();
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			Lesson entity = lessonRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy lesson"));
			lessonRepository.deleteById(id);
			User admin = getUserByToken.get();
			sendNotification.sendNotificationDelete(entity.getTitle(), entity.getUser().getId(), admin, Type.LESSON);
		} catch (EmptyResultDataAccessException e) {
			throw new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	public LessonResponse hide(Long id, HideRequest dto) {
		Lesson entity = lessonRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy lesson"));
		boolean tempHide = entity.isHide();
		entity.setHide(dto.isHide());
		Lesson saved = lessonRepository.save(entity);

		User admin = getUserByToken.get();
		sendNotification.sendNotificationHide(dto.isHide(), tempHide, entity.getTitle(), entity.getUser().getId(),
				admin, Type.LESSON);
		return lessonMapper.lessonToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public LessonResponse update(Long id, LessonRequest dto) {
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
		return lessonMapper.lessonToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public File loadLessonFile(Long id) throws IOException {
		Lesson lesson = lessonRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy bài giàng"));
		return loadVideoFile(lesson);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public FileResponse loadAnyDocumentFile(Long id) throws IOException {
		Lesson doc = lessonRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));
		return loadDocumentFile(doc);
	}

	@PreAuthorize("hasAuthority('UPLOAD_LESSON')")
	@Transactional
	public LessonResponse uploadLesson(MultipartFile video, MultipartFile document, MultipartFile subFile,
			LessonRequest dto) throws IOException {
		Lesson lesson = lessonMapper.requestToLesson(dto);
		lesson.setCreatedAt(LocalDateTime.now());

		String lessonUrl = handleVideo(video);
		lesson.setLessonUrl(lessonUrl);

		if (document != null) {
			String documentUrl = handlefile(document);
			lesson.setDocumentUrl(documentUrl);
		}

		if (subFile != null) {
			String subFileUrl = handleSubFile(subFile);
			lesson.setSubFileUrl(subFileUrl);
		}

		String thumbnailUrl = handleThumbnail(videoStorage + "\\" + lessonUrl);
		lesson.setThumbnailUrl(thumbnailUrl);

		Category category = dto.getCategoryId() != null ? categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new AppException("category không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		lesson.setCategory(category);

		User user = getUserByToken.get();
		lesson.setUser(user);

		Lesson saved = lessonRepository.save(lesson);
		LessonResponse response = lessonMapper.lessonToResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('DOWNLOAD_LESSON_DOCUMENT')")
	public FileResponse downloadDocumentByLessonId(Long lessonId) throws IOException {

		Lesson lesson = lessonRepository.findByIdAndStatusAndHideFalse(lessonId, Status.PUBLISHED)
				.orElseThrow(() -> new AppException("Lesson không tồn tại", 1001, HttpStatus.NOT_FOUND));

		if (lesson.getDocumentUrl() == null) {
			throw new AppException("Lesson không có tài liệu", 1001, HttpStatus.BAD_REQUEST);
		}

		File file = new File(documentStorage + File.separator + lesson.getDocumentUrl());

		if (!file.exists()) {
			throw new AppException("File không tồn tại trong hệ thống", 1001, HttpStatus.NOT_FOUND);
		}

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		String downloadName = lesson.getTitle() + ".pdf";

		return new FileResponse(resource, file.length(), MediaType.APPLICATION_PDF, downloadName);
	}

	@PreAuthorize("hasAuthority('DOWNLOAD_LESSON_SUBFILE')")
	public FileResponse downloadSubFileByLessonId(Long lessonId) throws IOException {

		Lesson lesson = lessonRepository.findByIdAndStatusAndHideFalse(lessonId, Status.PUBLISHED)
				.orElseThrow(() -> new AppException("Lesson không tồn tại", 1001, HttpStatus.NOT_FOUND));

		if (lesson.getSubFileUrl() == null) {
			throw new AppException("Lesson không có file đính kèm", 1001, HttpStatus.BAD_REQUEST);
		}

		File file = new File(subfileStorage + File.separator + lesson.getSubFileUrl());

		if (!file.exists()) {
			throw new AppException("File không tồn tại trong hệ thống", 1001, HttpStatus.NOT_FOUND);
		}

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		return new FileResponse(resource, file.length(), MediaType.APPLICATION_OCTET_STREAM, lesson.getSubFileUrl());
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

	public List<LessonFavoriteResponse> getAllLessonsByUserheckFavorite(Long authorId) {
		User user = getUserByToken.get();
		if (user == null) {
			return lessonRepository.findAllLessonsByUserWithoutFavorite(authorId);
		}
		return lessonRepository.findAllLessonsByUserWithFavoriteStatus(authorId, user.getId());
	}

	@PreAuthorize("hasAuthority('GET_MY_LESSON')")
	public List<LessonResponse> getMyLesson() {
		User user = getUserByToken.get();
		List<Lesson> lessons = lessonRepository.findByUser_Id(user.getId());
		List<LessonResponse> response = new ArrayList<LessonResponse>();
		for (Lesson l : lessons) {
			response.add(lessonMapper.lessonToResponse(l));
		}
		return response;
	}

	@PreAuthorize("hasAuthority('UPDATE_MY_LESSON')")
	public LessonResponse updateMyDocument(Long id, LessonRequest dto) {
		User user = getUserByToken.get();
		Lesson entity = lessonRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		boolean tempHide = entity.isHide();
		lessonMapper.updateLesson(entity, dto);
		entity.setUpdatedAt(LocalDateTime.now());
		Lesson saved = lessonRepository.save(entity);
		sendNotification.sendNotificationMyHide(dto.isHide(), tempHide, entity.getTitle(), user.getId(),
				user.getUsername(), Type.LESSON);
		return lessonMapper.lessonToResponse(saved);
	}

	@PreAuthorize("hasAuthority('DELETE_MY_LESSON')")
	public void deleteMyLesson(Long id) {
		try {
			User user = getUserByToken.get();
			Lesson entity = lessonRepository.findByIdAndUser_Id(id, user.getId())
					.orElseThrow(() -> new RuntimeException("Không tìm thấy document"));
			fileStorage.deleteFile(videoStorage + File.separator + entity.getLessonUrl());
			fileStorage.deleteFile(thumbnailStorage + File.separator + entity.getThumbnailUrl());

			if (entity.getDocumentUrl() != null) {
				fileStorage.deleteFile(documentStorage + File.separator + entity.getDocumentUrl());
			}

			if (entity.getSubFileUrl() != null) {
				fileStorage.deleteFile(documentStorage + File.separator + entity.getSubFileUrl());
			}

			lessonRepository.deleteById(id);

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

	private File loadVideoFile(Lesson lesson) {

		File file = new File(videoStorage + File.separator + lesson.getLessonUrl());

		if (!file.exists()) {
			throw new RuntimeException("File không tồn tại trong hệ thống");
		}

		return file;
	}

	private FileResponse loadDocumentFile(Lesson doc) throws IOException {

		String filePath = documentStorage + File.separator + doc.getDocumentUrl();

		File file = new File(filePath);

		if (!file.exists()) {
			throw new RuntimeException("File không tồn tại trong hệ thống");
		}

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		return new FileResponse(resource, file.length(), MediaType.APPLICATION_PDF, doc.getTitle());
	}

	private String handleVideo(MultipartFile video) throws IOException {
		String fileName = video.getOriginalFilename();
		if (fileName.endsWith(".mp4")) {
			String fileUrl = fileStorage.saveFile(video, videoStorage);
			return fileUrl;
		} else {
			throw new AppException("video không hợp lệ", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	private String handlefile(MultipartFile document) throws IOException {
		String fileName = document.getOriginalFilename();
		if (fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx")
				|| fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {

			String fileUrl = fileStorage.saveFile(document, documentStorage);
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

	private String handleSubFile(MultipartFile subFile) throws IOException {
		String fileName = subFile.getOriginalFilename();
		if (fileName.endsWith(".rar")) {
			String fileUrl = fileStorage.saveFile(subFile, subfileStorage);
			return fileUrl;
		} else {
			throw new AppException("file không hợp lệ", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	private String handleThumbnail(String input) {
		FFmpegFrameGrabber grabber = null;

		String thumbnailUrl = UUID.randomUUID().toString() + ".jpg";
		String output = thumbnailStorage + File.separator + thumbnailUrl;

		try {
			grabber = new FFmpegFrameGrabber(input);
			grabber.start();

			Frame frame;
			BufferedImage image = null;

			// Bỏ qua frame audio, chỉ lấy frame hình
			while ((frame = grabber.grab()) != null) {
				if (frame.image != null) {
					try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
						image = converter.convert(frame);
					}
					break;
				}
			}

			if (image == null) {
				throw new RuntimeException("Không lấy được frame hình từ video");
			}

			ImageIO.write(image, "jpg", new File(output));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (grabber != null)
					grabber.stop();
			} catch (Exception ignored) {
			}
		}

		return thumbnailUrl;
	}
}

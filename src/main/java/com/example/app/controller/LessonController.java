package com.example.app.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.constant.AppError;
import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.lesson.LessonDetailResponse;
import com.example.app.dto.response.lesson.LessonResponse;
import com.example.app.dto.response.lesson.LessonStatsResponse;
import com.example.app.dto.response.lesson.LessonUserResponse;
import com.example.app.exception.AppException;
import com.example.app.service.LessonService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/lessons")
@AllArgsConstructor
public class LessonController {
	private final LessonService lessonService;

	@GetMapping("/stats")
	public APIResponse<LessonStatsResponse> getStats() {
		APIResponse<LessonStatsResponse> apiResponse = new APIResponse<LessonStatsResponse>();
		apiResponse.setResult(lessonService.getStats());
		return apiResponse;
	}

	@GetMapping("/search")
	public APIResponse<LessonResponse> search(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long categoryId) {
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(lessonService.search(keyword, categoryId));
		return apiResponse;
	}

	@GetMapping("/{id}")
	public APIResponse<LessonDetailResponse> getByIdPublicLesson(@PathVariable Long id) {
		APIResponse<LessonDetailResponse> apiResponse = new APIResponse<LessonDetailResponse>();
		apiResponse.setResult(lessonService.findByIdPublicLesson(id));
		return apiResponse;
	}

	@GetMapping
	public APIResponse<LessonResponse> getAllPublicLesson() {
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(lessonService.getAllPublicLessonsCheckFavorite());
		return apiResponse;
	}

	@GetMapping("/user")
	public APIResponse<LessonResponse> getByUser(@RequestParam Long lessonId, @RequestParam Long userId) {
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(lessonService.getLessonsByUserCheckFavorite(lessonId, userId));
		return apiResponse;
	}

	@GetMapping("/user/{userId}")
	public APIResponse<LessonResponse> getAllLessonsByUser(@PathVariable Long userId) {
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(lessonService.getAllLessonsByUserCheckFavorite(userId));
		return apiResponse;
	}

	@GetMapping("/category")
	public APIResponse<LessonResponse> getByCategory(@RequestParam Long categoryId, @RequestParam Long lessonId) {
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(lessonService.getLessonsByCategoryCheckFavorite(categoryId, lessonId));
		return apiResponse;
	}

	@PostMapping("/view/{id}")
	public APIResponse<Void> increaseView(@PathVariable Long id) {
		lessonService.increaseView(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		return apiResponse;
	}

	@GetMapping("/count/{userId}")
	public APIResponse<Long> countDocumentOfUser(@PathVariable Long userId) {
		APIResponse<Long> apiResponse = new APIResponse<Long>();
		apiResponse.setResult(lessonService.countLessonOfUser(userId));
		return apiResponse;
	}

	@PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public APIResponse<LessonDetailResponse> create(@RequestPart("video") MultipartFile video,
			@RequestPart(value = "document", required = false) MultipartFile document,
			@RequestPart(value = "subfile", required = false) MultipartFile subfile,
			@RequestPart("data") String dataJson) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			LessonRequest dto = mapper.readValue(dataJson, LessonRequest.class);

			APIResponse<LessonDetailResponse> apiResponse = new APIResponse<LessonDetailResponse>();
			apiResponse.setResult(lessonService.uploadLesson(video, document, subfile, dto));
			return apiResponse;

		} catch (Exception e) {
			throw AppException.builder().appError(AppError.INVALID_JSON_FORMAT).build();
		}
	}

	@GetMapping("/{id}/download-document")
	public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws Exception {

		FileResponse file = lessonService.downloadDocumentByLessonId(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
				.body(file.getResource());
	}

	@GetMapping("/{id}/download-subfile")
	public ResponseEntity<Resource> downloadSubFile(@PathVariable Long id) throws Exception {

		FileResponse file = lessonService.downloadSubFileByLessonId(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
				.body(file.getResource());
	}

	@GetMapping("/my-lesson")
	public APIResponse<LessonUserResponse> getMylesson() {
		APIResponse<LessonUserResponse> apiResponse = new APIResponse<LessonUserResponse>();
		apiResponse.setResultList(lessonService.getMyLesson());
		return apiResponse;
	}

	@GetMapping("/my-lesson/{id}")
	public APIResponse<LessonDetailResponse> getMyLessonDetail(@PathVariable Long id) {
		APIResponse<LessonDetailResponse> apiResponse = new APIResponse<LessonDetailResponse>();
		apiResponse.setResult(lessonService.getMyLessonDetail(id));
		return apiResponse;
	}

	@PutMapping("my-lesson/{id}")
	public APIResponse<LessonUserResponse> updateMyLesson(@PathVariable Long id,
			@RequestBody @Valid LessonRequest dto) {
		APIResponse<LessonUserResponse> apiResponse = new APIResponse<LessonUserResponse>();
		apiResponse.setResult(lessonService.updateMyLesson(id, dto));
		return apiResponse;
	}

	@DeleteMapping("my-lesson/{id}")
	public APIResponse<Void> deleteMyLesson(@PathVariable Long id) {
		lessonService.deleteMyLesson(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		return apiResponse;
	}

	@GetMapping("/my-lesson/count")
	public APIResponse<Long> CountMyLesson() {
		APIResponse<Long> apiResponse = new APIResponse<Long>();
		apiResponse.setResult(lessonService.countMyLesson());
		return apiResponse;
	}

}

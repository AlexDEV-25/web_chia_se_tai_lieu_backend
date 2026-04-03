package com.example.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.lesson.LessonDetailResponse;
import com.example.app.dto.response.lesson.LessonFavoriteResponse;
import com.example.app.dto.response.lesson.LessonStatsResponse;
import com.example.app.dto.response.lesson.LessonUserResponse;
import com.example.app.exception.AppException;
import com.example.app.service.LessonService;
import com.example.app.share.FileManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/lessons")
@AllArgsConstructor
public class LessonController {
	private final LessonService lessonService;
	private final FileManager fileManager;

	@GetMapping("/stats")
	public APIResponse<LessonStatsResponse> getStats() {
		LessonStatsResponse response = lessonService.getStats();
		APIResponse<LessonStatsResponse> apiResponse = new APIResponse<LessonStatsResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping("/search")
	public APIResponse<LessonFavoriteResponse> search(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long categoryId) {
		List<LessonFavoriteResponse> response = lessonService.search(keyword, categoryId);
		APIResponse<LessonFavoriteResponse> apiResponse = new APIResponse<LessonFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/{id}")
	public APIResponse<LessonDetailResponse> getByIdPublicLesson(@PathVariable Long id) {
		LessonDetailResponse response = lessonService.findByIdPublicLesson(id);
		APIResponse<LessonDetailResponse> apiResponse = new APIResponse<LessonDetailResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<LessonFavoriteResponse> getAllPublicLesson() {
		List<LessonFavoriteResponse> response = lessonService.getAllPublicLessonsCheckFavorite();
		APIResponse<LessonFavoriteResponse> apiResponse = new APIResponse<LessonFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/user")
	public APIResponse<LessonFavoriteResponse> getByUser(@RequestParam Long lessonId, @RequestParam Long userId) {
		List<LessonFavoriteResponse> response = lessonService.getLessonsByUserCheckFavorite(lessonId, userId);
		APIResponse<LessonFavoriteResponse> apiResponse = new APIResponse<LessonFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/user/{userId}")
	public APIResponse<LessonFavoriteResponse> getAllLessonsByUser(@PathVariable Long userId) {
		List<LessonFavoriteResponse> response = lessonService.getAllLessonsByUserCheckFavorite(userId);
		APIResponse<LessonFavoriteResponse> apiResponse = new APIResponse<LessonFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/category")
	public APIResponse<LessonFavoriteResponse> getByCategory(@RequestParam Long categoryId,
			@RequestParam Long lessonId) {
		List<LessonFavoriteResponse> response = lessonService.getLessonsByCategoryCheckFavorite(categoryId, lessonId);
		APIResponse<LessonFavoriteResponse> apiResponse = new APIResponse<LessonFavoriteResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PostMapping("/view/{id}")
	public APIResponse<Void> increaseView(@PathVariable Long id) {
		lessonService.increaseView(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("increase success");
		return apiResponse;
	}

	@GetMapping("/count/{userId}")
	public APIResponse<Long> countDocumentOfUser(@PathVariable Long userId) {
		Long num = lessonService.countLessonOfUser(userId);
		APIResponse<Long> apiResponse = new APIResponse<Long>();
		apiResponse.setResult(num);
		apiResponse.setMessage("increase success");
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
			LessonDetailResponse response = lessonService.uploadLesson(video, document, subfile, dto);
			APIResponse<LessonDetailResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thành công");
			apiResponse.setResult(response);
			return apiResponse;

		} catch (Exception e) {
			throw new AppException("Upload Thất bại", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/download-document")
	public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws IOException {

		FileResponse file = lessonService.downloadDocumentByLessonId(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
				.body(file.getResource());
	}

	@GetMapping("/{id}/download-subfile")
	public ResponseEntity<Resource> downloadSubFile(@PathVariable Long id) throws IOException {

		FileResponse file = lessonService.downloadSubFileByLessonId(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
				.body(file.getResource());
	}

	@GetMapping("/{id}/video")
	public ResponseEntity<Resource> getPublicLessonVideo(@PathVariable Long id, @RequestHeader HttpHeaders headers)
			throws IOException {
		File video = lessonService.loadPublicLessonFile(id);
		return fileManager.getVideo(video, headers);
	}

	@GetMapping("/{id}/document")
	public ResponseEntity<Resource> loadPublicDocument(@PathVariable Long id) throws IOException {

		FileResponse file = lessonService.loadPublicDocumentFile(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.body(file.getResource());
	}

	@GetMapping("/admin/{id}/document")
	public ResponseEntity<Resource> loadAnyDocumentFile(@PathVariable Long id) throws IOException {

		FileResponse file = lessonService.loadAnyDocumentFile(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.body(file.getResource());
	}

	@GetMapping("/my-lesson")
	public APIResponse<LessonUserResponse> getMylesson() {
		List<LessonUserResponse> response = lessonService.getMyLesson();
		APIResponse<LessonUserResponse> apiResponse = new APIResponse<LessonUserResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("my-lesson/{id}")
	public APIResponse<LessonUserResponse> updateMyLesson(@PathVariable Long id,
			@RequestBody @Valid LessonRequest dto) {
		LessonUserResponse response = lessonService.updateMyDocument(id, dto);
		APIResponse<LessonUserResponse> apiResponse = new APIResponse<LessonUserResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@DeleteMapping("my-lesson/{id}")
	public APIResponse<Void> deleteMyLesson(@PathVariable Long id) {
		lessonService.deleteMyLesson(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@GetMapping("/my-lesson/count")
	public APIResponse<Long> CountMyLesson() {
		Long response = lessonService.countMyLesson();
		APIResponse<Long> apiResponse = new APIResponse<Long>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

}

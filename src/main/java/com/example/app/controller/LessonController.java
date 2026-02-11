package com.example.app.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
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

import com.example.app.dto.request.HideRequest;
import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.LessonResponse;
import com.example.app.dto.response.LessonStatsResponse;
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
		LessonStatsResponse response = lessonService.getStats();
		APIResponse<LessonStatsResponse> apiResponse = new APIResponse<LessonStatsResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping("/search")
	public APIResponse<LessonResponse> search(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Long categoryId) {
		List<LessonResponse> response = lessonService.search(keyword, categoryId);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/admin/{id}")
	public APIResponse<LessonResponse> getById(@PathVariable Long id) {
		LessonResponse response = lessonService.findById(id);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping("/{id}")
	public APIResponse<LessonResponse> getByIdPublicLesson(@PathVariable Long id) {
		LessonResponse response = lessonService.findByIdPublicLesson(id);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping("/admin")
	public APIResponse<LessonResponse> getAll() {
		List<LessonResponse> response = lessonService.getAllLessons();
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<LessonResponse> getAllPublicLesson() {
		List<LessonResponse> response = lessonService.getAllPublicLessons();
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@DeleteMapping("/admin/{id}")
	public APIResponse<LessonResponse> delete(@PathVariable Long id) {
		lessonService.delete(id);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@GetMapping("/user")
	public APIResponse<LessonResponse> getByUser(@RequestParam Long lessonId, @RequestParam Long userId) {
		List<LessonResponse> response = lessonService.getByUser(lessonId, userId);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/user/{userId}")
	public APIResponse<LessonResponse> getAlLessonsByUser(@PathVariable Long userId) {
		List<LessonResponse> response = lessonService.getAlLessonsByUser(userId);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/category")
	public APIResponse<LessonResponse> getByCategory(@RequestParam Long lessonId, @RequestParam Long categoryId) {
		List<LessonResponse> response = lessonService.getByCategory(lessonId, categoryId);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("/admin/hide/{id}")
	public APIResponse<LessonResponse> hide(@PathVariable Long id, @RequestBody @Valid HideRequest dto) {
		LessonResponse response = lessonService.hide(id, dto);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}

	@PutMapping("admin/{id}")
	public APIResponse<LessonResponse> update(@PathVariable Long id, @RequestBody LessonRequest dto) {
		LessonResponse response = lessonService.update(id, dto);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@PostMapping("/view/{id}")
	public APIResponse<Void> increaseView(@PathVariable Long id) {
		lessonService.increaseView(id);
		APIResponse<Void> apiResponse = new APIResponse<Void>();
		apiResponse.setMessage("increase success");
		return apiResponse;
	}

	@PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public APIResponse<LessonResponse> create(@RequestPart("video") MultipartFile video,
			@RequestPart(value = "document", required = false) MultipartFile document,
			@RequestPart(value = "subfile", required = false) MultipartFile subfile,
			@RequestPart("data") String dataJson) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			LessonRequest dto = mapper.readValue(dataJson, LessonRequest.class);
			LessonResponse response = lessonService.uploadLesson(video, document, subfile, dto);
			APIResponse<LessonResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thành công");
			apiResponse.setResult(response);
			return apiResponse;

		} catch (Exception e) {
			APIResponse<LessonResponse> apiResponse = new APIResponse<>();
			apiResponse.setMessage("Upload thất bại: " + e.getMessage());
			return apiResponse;
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
		return getVideo(video, headers);
	}

	@GetMapping("admin/{id}/video")
	public ResponseEntity<Resource> getLessonVideo(@PathVariable Long id, @RequestHeader HttpHeaders headers)
			throws IOException {
		File video = lessonService.loadLessonFile(id);
		return getVideo(video, headers);
	}

	@GetMapping("/{id}/document")
	public ResponseEntity<Resource> loadPublicDocument(@PathVariable Long id) throws IOException {

		FileResponse file = lessonService.loadPublicDocumentFile(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.body(file.getResource());
	}

	@GetMapping("/admin/{id}/document")
	public ResponseEntity<Resource> loadDocument(@PathVariable Long id) throws IOException {

		FileResponse file = lessonService.loadDocumentFile(id);

		return ResponseEntity.ok().contentLength(file.getLength()).contentType(file.getMediaType())
				.body(file.getResource());
	}

	@GetMapping("/my-lesson")
	public APIResponse<LessonResponse> getMylesson() {
		List<LessonResponse> response = lessonService.getMyLesson();
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("my-lesson/{id}")
	public APIResponse<LessonResponse> updateMyLesson(@PathVariable Long id, @RequestBody @Valid LessonRequest dto) {
		LessonResponse response = lessonService.updateMyDocument(id, dto);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@DeleteMapping("my-lesson/{id}")
	public APIResponse<LessonResponse> deleteMyLesson(@PathVariable Long id) {
		lessonService.deleteMyLesson(id);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	private ResponseEntity<Resource> getVideo(File video, @RequestHeader HttpHeaders headers) throws IOException {

		long fileLength = video.length();

		String range = headers.getFirst(HttpHeaders.RANGE);

		// 👉 Trường hợp browser chưa seek
		if (range == null) {
			return ResponseEntity.ok().contentType(MediaType.valueOf("video/mp4")).contentLength(fileLength)
					.header(HttpHeaders.ACCEPT_RANGES, "bytes").body(new FileSystemResource(video));
		}

		// 👉 Browser yêu cầu seek
		long start = Long.parseLong(range.replace("bytes=", "").replace("-", ""));
		long end = fileLength - 1;
		long contentLength = end - start + 1;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(HttpHeaders.CONTENT_TYPE, "video/mp4");
		responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
		responseHeaders.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);
		responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));

		InputStream inputStream = new FileInputStream(video);
		inputStream.skip(start);

		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(responseHeaders)
				.body(new InputStreamResource(inputStream));
	}
}

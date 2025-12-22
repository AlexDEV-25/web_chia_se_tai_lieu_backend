package com.example.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
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

import com.example.app.dto.request.HideRequest;
import com.example.app.dto.request.LessonRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.FileResponse;
import com.example.app.dto.response.LessonResponse;
import com.example.app.service.LessonService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/lessons")
@AllArgsConstructor
public class LessonController {
	private final LessonService lessonService;

	@GetMapping("/{id}")
	public APIResponse<LessonResponse> getById(@PathVariable Long id) {
		LessonResponse response = lessonService.findById(id);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<LessonResponse> getAll() {
		List<LessonResponse> response = lessonService.getAllLessons();
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<LessonResponse> delete(@PathVariable Long id) {
		lessonService.delete(id);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<LessonResponse> update(@PathVariable Long id, @RequestBody LessonRequest dto) {
		LessonResponse response = lessonService.update(id, dto);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@GetMapping("/user/{userId}")
	public APIResponse<LessonResponse> getByUser(@PathVariable Long userId) {
		List<LessonResponse> response = lessonService.getByUser(userId);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/category/{categoryId}")
	public APIResponse<LessonResponse> getByCategory(@PathVariable Long categoryId) {
		List<LessonResponse> response = lessonService.getByCategory(categoryId);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("hide/{id}")
	public APIResponse<LessonResponse> hide(@PathVariable Long id, @RequestBody HideRequest dto) {
		LessonResponse response = lessonService.hide(id, dto);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}

	@PutMapping("status/{id}")
	public APIResponse<LessonResponse> changeStatus(@PathVariable Long id, @RequestBody LessonRequest dto) {
		LessonResponse response = lessonService.changeStatus(id, dto);
		APIResponse<LessonResponse> apiResponse = new APIResponse<LessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
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

	@GetMapping(value = "/download-document")
	public ResponseEntity<Resource> downloadDocument(@RequestParam("fileName") String filename) {
		try {
			File fileToDownload = lessonService.getDownloadDocument(filename);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.contentLength(fileToDownload.length()).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping(value = "/download-subfile")
	public ResponseEntity<Resource> downloadSubFile(@RequestParam("fileName") String filename) {
		try {
			File fileToDownload = lessonService.getDownloadSubFile(filename);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.contentLength(fileToDownload.length()).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping(value = "/{id}/video")
	public ResponseEntity<Resource> getLessonVideo(@PathVariable Long id) throws IOException {

		FileResponse fileResponse = lessonService.loadVideo(id);

		return ResponseEntity.ok().contentType(fileResponse.getMediaType()).contentLength(fileResponse.getLength())
				.body(fileResponse.getResource());
	}

	@GetMapping("/{id}/document")
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
	public APIResponse<LessonResponse> updateMyLesson(@PathVariable Long id, @RequestBody LessonRequest dto) {
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
}

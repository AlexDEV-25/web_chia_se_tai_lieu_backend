package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.CommentDocumentRequest;
import com.example.app.dto.request.CommentLessonRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.CommentDocumentResponse;
import com.example.app.dto.response.CommentLessonResponse;
import com.example.app.service.CommentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@GetMapping("/{id}")
	public APIResponse<CommentDocumentResponse> getById(@PathVariable Long id) {
		CommentDocumentResponse response = commentService.findById(id);
		APIResponse<CommentDocumentResponse> apiResponse = new APIResponse<CommentDocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<CommentDocumentResponse> getAll() {
		List<CommentDocumentResponse> response = commentService.getAllComments();
		APIResponse<CommentDocumentResponse> apiResponse = new APIResponse<CommentDocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PostMapping("/document")
	public APIResponse<CommentDocumentResponse> createCommentDocument(@RequestBody @Valid CommentDocumentRequest dto) {
		CommentDocumentResponse response = commentService.saveCommentDocument(dto);
		APIResponse<CommentDocumentResponse> apiResponse = new APIResponse<CommentDocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PostMapping("/lesson")
	public APIResponse<CommentLessonResponse> createCommentLesson(@RequestBody @Valid CommentLessonRequest dto) {
		CommentLessonResponse response = commentService.saveCommentLesson(dto);
		APIResponse<CommentLessonResponse> apiResponse = new APIResponse<CommentLessonResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<CommentDocumentResponse> update(@PathVariable Long id, @RequestBody CommentDocumentRequest dto) {
		CommentDocumentResponse response = commentService.update(id, dto);
		APIResponse<CommentDocumentResponse> apiResponse = new APIResponse<CommentDocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@PutMapping("hide/{id}")
	public APIResponse<CommentDocumentResponse> hide(@PathVariable Long id, @RequestBody HideRequest dto) {
		CommentDocumentResponse response = commentService.hide(id, dto);
		APIResponse<CommentDocumentResponse> apiResponse = new APIResponse<CommentDocumentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<CommentDocumentResponse> delete(@PathVariable Long id) {
		commentService.delete(id);
		APIResponse<CommentDocumentResponse> apiResponse = new APIResponse<CommentDocumentResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@GetMapping("/document/{docId}")
	public APIResponse<CommentDocumentResponse> getByDocument(@PathVariable Long docId) {
		List<CommentDocumentResponse> response = commentService.getByDocument(docId);
		APIResponse<CommentDocumentResponse> apiResponse = new APIResponse<CommentDocumentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson/{lessonId}")
	public APIResponse<CommentLessonResponse> getByLesson(@PathVariable Long lessonId) {
		List<CommentLessonResponse> response = commentService.getByLesson(lessonId);
		APIResponse<CommentLessonResponse> apiResponse = new APIResponse<CommentLessonResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}
}

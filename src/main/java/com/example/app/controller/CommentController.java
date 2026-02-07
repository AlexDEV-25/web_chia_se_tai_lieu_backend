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

import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.CommentResponse;
import com.example.app.dto.response.CommentTreeResponse;
import com.example.app.service.CommentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@GetMapping("/admin/{id}")
	public APIResponse<CommentResponse> getById(@PathVariable Long id) {
		CommentResponse response = commentService.findById(id);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
		return apiResponse;
	}

	@GetMapping("/admin")
	public APIResponse<CommentResponse> getAll() {
		List<CommentResponse> response = commentService.getAllComments();
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/document/{docId}")
	public APIResponse<List<CommentTreeResponse>> getDocumentCommentTree(@PathVariable Long docId) {
		List<CommentTreeResponse> response = commentService.getDocumentCommentTree(docId);
		APIResponse<List<CommentTreeResponse>> apiResponse = new APIResponse<List<CommentTreeResponse>>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@GetMapping("/lesson/{lessonId}")
	public APIResponse<List<CommentTreeResponse>> getLessonCommentTree(@PathVariable Long lessonId) {
		List<CommentTreeResponse> response = commentService.getLessonCommentTree(lessonId);
		APIResponse<List<CommentTreeResponse>> apiResponse = new APIResponse<List<CommentTreeResponse>>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PostMapping("/document")
	public APIResponse<CommentResponse> createCommentDocument(@RequestBody @Valid CommentRequest dto) {
		CommentResponse response = commentService.saveCommentDocument(dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PostMapping("/lesson")
	public APIResponse<CommentResponse> createCommentLesson(@RequestBody @Valid CommentRequest dto) {
		CommentResponse response = commentService.saveCommentLesson(dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PutMapping("/admin/hide/{id}")
	public APIResponse<CommentResponse> hide(@PathVariable Long id, @RequestBody @Valid HideRequest dto) {
		CommentResponse response = commentService.hide(id, dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}

	@DeleteMapping("/admin/{id}")
	public APIResponse<CommentResponse> delete(@PathVariable Long id) {
		commentService.delete(id);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<CommentResponse> update(@PathVariable Long id, @RequestBody @Valid CommentRequest dto) {
		CommentResponse response = commentService.update(id, dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}
}

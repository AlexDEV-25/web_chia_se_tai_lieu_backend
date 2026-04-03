package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.comment.CommentResponse;
import com.example.app.dto.response.comment.CommentTreeResponse;
import com.example.app.service.CommentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {
	private final CommentService commentService;

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

	@PostMapping
	public APIResponse<CommentResponse> createMyComment(@RequestBody @Valid CommentRequest dto) {
		CommentResponse response = commentService.saveMyComment(dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<CommentResponse> updateMyComment(@PathVariable Long id, @RequestBody @Valid CommentRequest dto) {
		CommentResponse response = commentService.updateMyComment(id, dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@PutMapping("/hide/{id}")
	public APIResponse<CommentResponse> hideMyComment(@PathVariable Long id) {
		CommentResponse response = commentService.hideMyComment(id);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}
}

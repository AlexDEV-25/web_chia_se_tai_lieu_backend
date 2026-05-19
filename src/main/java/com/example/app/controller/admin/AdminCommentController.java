package com.example.app.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.DisplayRequest;
import com.example.app.dto.response.APIResponse;
import com.example.app.dto.response.comment.CommentResponse;
import com.example.app.service.ChatService;
import com.example.app.service.CommentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comments/admin")
@AllArgsConstructor
public class AdminCommentController {
	private final ChatService chatService;
	private final CommentService commentService;

	@GetMapping("/filter-comment")
	APIResponse<CommentResponse> filterCommnent(@RequestParam String type) {
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResultList(chatService.filterCommnent(type));
		return apiResponse;
	}

	@GetMapping("/document")
	public APIResponse<CommentResponse> getAllDocumentComments() {
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResultList(commentService.getAllDocumentComments());
		return apiResponse;
	}

	@GetMapping("/lesson")
	public APIResponse<CommentResponse> getAllLessonComments() {
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResultList(commentService.getAllLessonComments());
		return apiResponse;
	}

	@PutMapping("/hide/{id}")
	public APIResponse<CommentResponse> hide(@PathVariable Long id, @RequestBody @Valid DisplayRequest dto) {
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(commentService.hide(id, dto));
		return apiResponse;
	}
}

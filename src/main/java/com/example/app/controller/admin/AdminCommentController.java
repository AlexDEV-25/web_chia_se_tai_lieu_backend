package com.example.app.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.request.HideRequest;
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
	APIResponse<CommentResponse> filterCommnent() {
		List<CommentResponse> response = chatService.filterCommnent();
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("chat success");
		return apiResponse;
	}

	@GetMapping
	public APIResponse<CommentResponse> getAll() {
		List<CommentResponse> response = commentService.getAllComments();
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}

	@PutMapping("/hide/{id}")
	public APIResponse<CommentResponse> hide(@PathVariable Long id, @RequestBody @Valid HideRequest dto) {
		CommentResponse response = commentService.hide(id, dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}
}

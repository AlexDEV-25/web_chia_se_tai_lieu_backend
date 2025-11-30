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
import com.example.app.service.CommentService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@GetMapping("/{id}")
	public APIResponse<CommentResponse> getById(@PathVariable Long id) {
		CommentResponse response = commentService.findById(id);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("get by id success");
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

	@PostMapping
	public APIResponse<CommentResponse> create(@RequestBody CommentRequest dto) {
		CommentResponse response = commentService.save(dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("save success");
		return apiResponse;
	}

	@PutMapping("/{id}")
	public APIResponse<CommentResponse> update(@PathVariable Long id, @RequestBody CommentRequest dto) {
		CommentResponse response = commentService.update(id, dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("update success");
		return apiResponse;
	}

	@PutMapping("hide/{id}")
	public APIResponse<CommentResponse> hide(@PathVariable Long id, @RequestBody HideRequest dto) {
		CommentResponse response = commentService.hide(id, dto);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResult(response);
		apiResponse.setMessage("hide success");
		return apiResponse;
	}

	@DeleteMapping("/{id}")
	public APIResponse<CommentResponse> delete(@PathVariable Long id) {
		commentService.delete(id);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setMessage("delete success");
		return apiResponse;
	}

	@GetMapping("/document/{docId}")
	public APIResponse<CommentResponse> getByDocument(@PathVariable Long docId) {
		List<CommentResponse> response = commentService.getByDocument(docId);
		APIResponse<CommentResponse> apiResponse = new APIResponse<CommentResponse>();
		apiResponse.setResultList(response);
		apiResponse.setMessage("get all success");
		return apiResponse;
	}
}

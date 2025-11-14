package com.example.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.model.Comment;
import com.example.app.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@GetMapping("/document/{docId}")
	public List<Comment> getByDocument(@PathVariable Long docId) {
		return commentService.getByDocument(docId);
	}

	@PostMapping
	public Comment create(@RequestBody Comment comment) {
		return commentService.save(comment);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		commentService.delete(id);
	}
}

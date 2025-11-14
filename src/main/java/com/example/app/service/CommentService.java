package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.model.Comment;
import com.example.app.repository.CommentRepository;

@Service
public class CommentService {
	private final CommentRepository commentRepository;

	public CommentService(CommentRepository commentRepository) {

		this.commentRepository = commentRepository;
	}

	public List<Comment> getByDocument(Long docId) {
		return commentRepository.findByDocumentId(docId);
	}

	public Comment save(Comment comment) {
		return commentRepository.save(comment);
	}

	public void delete(Long id) {
		commentRepository.deleteById(id);
	}
}

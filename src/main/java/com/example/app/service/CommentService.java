package com.example.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.CommentResponse;
import com.example.app.mapper.CommentMapper;
import com.example.app.model.Comment;
import com.example.app.model.Document;
import com.example.app.model.User;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	private CommentMapper commentMapper;

	public List<CommentResponse> getAllComments() {
		List<Comment> comments = commentRepository.findAll();
		List<CommentResponse> response = new ArrayList<CommentResponse>();
		for (Comment c : comments) {
			response.add(commentMapper.commentToResponse(c));
		}
		return response;
	}

	public CommentResponse findById(Long id) {
		Comment find = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy comment"));
		return commentMapper.commentToResponse(find);
	}

	public List<CommentResponse> getByDocument(Long docId) {
		List<Comment> comments = commentRepository.findByDocumentId(docId);
		List<CommentResponse> response = new ArrayList<CommentResponse>();
		for (Comment c : comments) {
			response.add(commentMapper.commentToResponse(c));
		}
		return response;
	}

	public CommentResponse save(CommentRequest dto) {
		Comment comment = commentMapper.requestToComment(dto);
		Document doc = documentRepository.findById(dto.getDocumentId())
				.orElseThrow(() -> new RuntimeException("Document not found"));

		User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
		comment.setDocument(doc);
		comment.setUser(user);
		Comment saved = commentRepository.save(comment);
		CommentResponse response = commentMapper.commentToResponse(saved);
		return response;
	}

	public CommentResponse update(Long id, CommentRequest dto) {
		Comment entity = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
		commentMapper.updateComment(entity, dto);
		Comment saved = commentRepository.save(entity);
		return commentMapper.commentToResponse(saved);
	}

	public CommentResponse hide(Long id, HideRequest dto) {
		Comment entity = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
		commentMapper.hideComment(entity, dto);
		Comment saved = commentRepository.save(entity);
		return commentMapper.commentToResponse(saved);
	}

	public void delete(Long id) {
		if (!commentRepository.existsById(id)) {
			throw new RuntimeException("Comment not found with id: " + id);
		}
		commentRepository.deleteById(id);
	}
}

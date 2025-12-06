package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.CommentResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.CommentMapper;
import com.example.app.model.Comment;
import com.example.app.model.Document;
import com.example.app.model.User;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	private final CommentMapper commentMapper;

	public List<CommentResponse> getByDocument(Long docId) {
		List<Comment> comments = commentRepository.findByDocumentId(docId);
		List<CommentResponse> response = new ArrayList<CommentResponse>();
		for (Comment c : comments) {
			response.add(commentMapper.commentToResponse(c));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<CommentResponse> getAllComments() {
		List<Comment> comments = commentRepository.findAll();
		List<CommentResponse> response = new ArrayList<CommentResponse>();
		for (Comment c : comments) {
			response.add(commentMapper.commentToResponse(c));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CommentResponse findById(Long id) {
		Comment find = commentRepository.findById(id)
				.orElseThrow(() -> new AppException("không tìm thấy comment", 1001, HttpStatus.BAD_REQUEST));
		return commentMapper.commentToResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CommentResponse update(Long id, CommentRequest dto) {
		Comment entity = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
		commentMapper.updateComment(entity, dto);
		entity.setUpdatedAt(LocalDateTime.now());
		Comment saved = commentRepository.save(entity);
		return commentMapper.commentToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CommentResponse hide(Long id, HideRequest dto) {
		Comment entity = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
		commentMapper.hideComment(entity, dto);
		Comment saved = commentRepository.save(entity);
		return commentMapper.commentToResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			commentRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new RuntimeException("Comment not found");
		}
	}

	@PreAuthorize("hasAuthority('POST_COMMENT')")
	public CommentResponse save(CommentRequest dto) {
		Comment comment = commentMapper.requestToComment(dto);
		Document doc = documentRepository.findById(dto.getDocumentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		comment.setDocument(doc);
		comment.setUser(user);
		comment.setCreatedAt(LocalDateTime.now());
		Comment saved = commentRepository.save(comment);
		CommentResponse response = commentMapper.commentToResponse(saved);
		return response;
	}
}

package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.CommentDocumentRequest;
import com.example.app.dto.request.CommentLessonRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.CommentDocumentResponse;
import com.example.app.dto.response.CommentLessonResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.CommentMapper;
import com.example.app.model.Comment;
import com.example.app.model.Document;
import com.example.app.model.Lesson;
import com.example.app.model.User;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final UserRepository userRepository;
	private final CommentMapper commentMapper;

	public List<CommentDocumentResponse> getByDocument(Long docId) {
		List<Comment> comments = commentRepository.findByDocumentId(docId);
		List<CommentDocumentResponse> response = new ArrayList<CommentDocumentResponse>();
		for (Comment c : comments) {
			response.add(commentMapper.commentToCommentDocumentResponse(c));
		}
		return response;
	}

	public List<CommentLessonResponse> getByLesson(Long lessonId) {
		List<Comment> comments = commentRepository.findByLessonId(lessonId);
		List<CommentLessonResponse> response = new ArrayList<CommentLessonResponse>();
		for (Comment c : comments) {
			response.add(commentMapper.commentToCommentLessonResponse(c));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<CommentDocumentResponse> getAllComments() {
		List<Comment> comments = commentRepository.findAll();
		List<CommentDocumentResponse> response = new ArrayList<CommentDocumentResponse>();
		for (Comment c : comments) {
			response.add(commentMapper.commentToCommentDocumentResponse(c));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CommentDocumentResponse findById(Long id) {
		Comment find = commentRepository.findById(id)
				.orElseThrow(() -> new AppException("không tìm thấy comment", 1001, HttpStatus.BAD_REQUEST));
		return commentMapper.commentToCommentDocumentResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CommentDocumentResponse update(Long id, CommentDocumentRequest request) {
		Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
		commentMapper.updateCommentDocument(comment, request);
		comment.setUpdatedAt(LocalDateTime.now());
		Comment saved = commentRepository.save(comment);
		return commentMapper.commentToCommentDocumentResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CommentDocumentResponse hide(Long id, HideRequest dto) {
		Comment entity = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
		entity.setHide(dto.isHide());
		Comment saved = commentRepository.save(entity);
		return commentMapper.commentToCommentDocumentResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		try {
			commentRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new RuntimeException("Comment not found");
		}
	}

	@PreAuthorize("hasAuthority('POST_LESSON_COMMENT')")
	public CommentDocumentResponse saveCommentDocument(CommentDocumentRequest dto) {
		Comment comment = commentMapper.commentDocumentRequestToComment(dto);
		Document doc = documentRepository.findById(dto.getDocumentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		comment.setDocument(doc);
		comment.setUser(user);
		comment.setCreatedAt(LocalDateTime.now());
		Comment saved = commentRepository.save(comment);
		CommentDocumentResponse response = commentMapper.commentToCommentDocumentResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('POST_LESSON_COMMENT')")
	public CommentLessonResponse saveCommentLesson(CommentLessonRequest dto) {
		Comment comment = commentMapper.commentLessonRequestToComment(dto);
		Lesson lesson = lessonRepository.findById(dto.getLessonId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		comment.setLesson(lesson);
		comment.setUser(user);
		comment.setCreatedAt(LocalDateTime.now());
		Comment saved = commentRepository.save(comment);
		CommentLessonResponse response = commentMapper.commentToCommentLessonResponse(saved);
		return response;
	}
}

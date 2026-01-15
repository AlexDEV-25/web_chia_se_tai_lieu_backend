package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.CommentResponse;
import com.example.app.dto.response.CommentTreeResponse;
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

	public List<CommentTreeResponse> getDocumentCommentTree(Long docId) {
		List<Comment> list = commentRepository.findByDocumentId(docId);
		Map<Long, CommentTreeResponse> map = new HashMap<>();

		for (Comment c : list) {
			if (c.isHide())
				continue;
			CommentTreeResponse dto = commentMapper.commentToCommentDocumentTreeResponse(c);
			map.put(dto.getId(), dto);
		}
		List<CommentTreeResponse> roots = builtTree(map);
		sortRecursively(roots);
		return roots;
	}

	public List<CommentTreeResponse> getLessonCommentTree(Long lessonId) {
		List<Comment> list = commentRepository.findByLessonId(lessonId);
		Map<Long, CommentTreeResponse> map = new HashMap<>();
		for (Comment c : list) {
			if (c.getIdParent() == null && c.isHide())
				continue;
			CommentTreeResponse dto = commentMapper.commentToCommentLessonTreeResponse(c);
			map.put(dto.getId(), dto);
		}

		List<CommentTreeResponse> roots = builtTree(map);
		sortRecursively(roots);
		return roots;
	}

	private List<CommentTreeResponse> builtTree(Map<Long, CommentTreeResponse> map) {
		List<CommentTreeResponse> roots = new ArrayList<CommentTreeResponse>();
		for (CommentTreeResponse dto : map.values()) {
			Long parentId = dto.getIdParent();
			if (parentId == null || parentId == 0) {
				roots.add(dto);
			} else {
				CommentTreeResponse parent = map.get(parentId);
				if (parent != null) {
					parent.getChildren().add(dto);
				}
			}
		}
		return roots;
	}

	private void sortRecursively(List<CommentTreeResponse> list) {
		if (list == null || list.isEmpty())
			return;
		list.sort(Comparator.comparing(CommentTreeResponse::getCreatedAt));
		for (CommentTreeResponse c : list) {
			sortRecursively(c.getChildren());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<CommentResponse> getAllComments() {
		List<Comment> comments = commentRepository.findAll();
		List<CommentResponse> response = new ArrayList<CommentResponse>();
		for (Comment c : comments) {
			response.add(commentMapper.commentToCommentDocumentResponse(c));
		}
		return response;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CommentResponse findById(Long id) {
		Comment find = commentRepository.findById(id)
				.orElseThrow(() -> new AppException("không tìm thấy comment", 1001, HttpStatus.BAD_REQUEST));
		return commentMapper.commentToCommentDocumentResponse(find);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CommentResponse update(Long id, CommentRequest request) {
		Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
		commentMapper.updateComment(comment, request);
		comment.setUpdatedAt(LocalDateTime.now());
		Comment saved = commentRepository.save(comment);
		return commentMapper.commentToCommentDocumentResponse(saved);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public CommentResponse hide(Long id, HideRequest dto) {
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
	public CommentResponse saveCommentDocument(CommentRequest dto) {
		Comment comment = commentMapper.commentRequestToComment(dto);
		Document doc = documentRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		comment.setDocument(doc);
		comment.setUser(user);
		comment.setCreatedAt(LocalDateTime.now());
		comment.setType(dto.getType());
		Comment saved = commentRepository.save(comment);
		CommentResponse response = commentMapper.commentToCommentDocumentResponse(saved);
		return response;
	}

	@PreAuthorize("hasAuthority('POST_LESSON_COMMENT')")
	public CommentResponse saveCommentLesson(CommentRequest dto) {
		Comment comment = commentMapper.commentRequestToComment(dto);
		Lesson lesson = lessonRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		comment.setLesson(lesson);
		comment.setUser(user);
		comment.setCreatedAt(LocalDateTime.now());
		comment.setType(dto.getType());
		Comment saved = commentRepository.save(comment);
		CommentResponse response = commentMapper.commentToCommentLessonResponse(saved);
		return response;
	}
}

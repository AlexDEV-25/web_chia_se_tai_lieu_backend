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
import com.example.app.share.NotificationType;
import com.example.app.share.SendNotification;
import com.example.app.share.Type;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final UserRepository userRepository;
	private final CommentMapper commentMapper;
	private final SendNotification sendNotification;

	public List<CommentTreeResponse> getDocumentCommentTree(Long docId) {
		List<Comment> comments = commentRepository.findByDocumentId(docId);
		List<Comment> cleaned = filterAndSort(comments);
		Map<Long, CommentTreeResponse> map = mapToTreeDto(cleaned, Type.DOCUMENT);
		return buildTreeFromMap(map);
	}

	public List<CommentTreeResponse> getLessonCommentTree(Long lessonId) {
		List<Comment> comments = commentRepository.findByLessonId(lessonId);
		List<Comment> cleaned = filterAndSort(comments);
		Map<Long, CommentTreeResponse> map = mapToTreeDto(cleaned, Type.LESSON);
		return buildTreeFromMap(map);
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
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new AppException("username không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		comment.setUser(user);

		Document doc = documentRepository.findById(dto.getContentId())
				.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));
		comment.setDocument(doc);

		comment.setCreatedAt(LocalDateTime.now());
		if (comment.getIdParent() == 0) {
			comment.setLevel(0L);
		} else {
			Comment find = commentRepository.findById(comment.getIdParent())
					.orElseThrow(() -> new AppException("không tìm thấy comment", 1001, HttpStatus.BAD_REQUEST));
			comment.setLevel(find.getLevel() + 1);
		}
		Comment saved = commentRepository.save(comment);
		CommentResponse response = commentMapper.commentToCommentDocumentResponse(saved);

		if (dto.getIdParent() != 0) {
			Comment cmt = commentRepository.findById(dto.getIdParent())
					.orElseThrow(() -> new AppException("comment không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			User receiver = cmt.getUser();
			if (receiver != null && user != null && !receiver.getId().equals(user.getId())) {
				Long NotificationId = sendNotification.saveNotification(
						"người dùng \" " + user.getUsername() + "\" đã trở lời bình luận của bạn",
						NotificationType.INFO);

				if (sendNotification.saveUserNotification(user.getId(), receiver.getId(), NotificationId) == false) {
					throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
				}
			}

		}
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
		if (comment.getIdParent() == 0) {
			comment.setLevel(0L);
		} else {
			Comment find = commentRepository.findById(comment.getIdParent())
					.orElseThrow(() -> new AppException("không tìm thấy comment", 1001, HttpStatus.BAD_REQUEST));
			comment.setLevel(find.getLevel() + 1);
		}
		Comment saved = commentRepository.save(comment);
		CommentResponse response = commentMapper.commentToCommentLessonResponse(saved);

		if (dto.getIdParent() != 0) {
			Comment cmt = commentRepository.findById(dto.getIdParent())
					.orElseThrow(() -> new AppException("comment không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			User receiver = cmt.getUser();
			if (receiver != null && user != null && !receiver.getId().equals(user.getId())) {
				Long NotificationId = sendNotification.saveNotification(
						"người dùng \" " + user.getUsername() + "\" đã trở lời bình luận của bạn",
						NotificationType.INFO);

				if (sendNotification.saveUserNotification(user.getId(), receiver.getId(), NotificationId) == false) {
					throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
				}
			}

		}
		return response;
	}

	private List<Comment> filterAndSort(List<Comment> comments) {
		return comments.stream().filter(c -> !c.isHide())
				.sorted(Comparator.comparing(Comment::getLevel).thenComparing(Comment::getCreatedAt)).toList();
	}

	private Map<Long, CommentTreeResponse> mapToTreeDto(List<Comment> comments, Type type) {
		Map<Long, CommentTreeResponse> map = new HashMap<>();
		if (type == Type.DOCUMENT) {
			for (Comment c : comments) {
				CommentTreeResponse dto = commentMapper.commentToCommentDocumentTreeResponse(c);
				map.put(dto.getId(), dto);
			}
		} else {
			for (Comment c : comments) {
				CommentTreeResponse dto = commentMapper.commentToCommentLessonTreeResponse(c);
				map.put(dto.getId(), dto);
			}
		}
		return map;
	}

	private List<CommentTreeResponse> buildTreeFromMap(Map<Long, CommentTreeResponse> map) {
		List<CommentTreeResponse> roots = new ArrayList<>();

		for (CommentTreeResponse dto : map.values()) {
			if (dto.getLevel() == 0) {
				roots.add(dto);
			} else {
				CommentTreeResponse parent = map.get(dto.getIdParent());
				if (parent != null) {
					parent.getChildren().add(dto);
				}
			}
		}
		return roots;
	}
}

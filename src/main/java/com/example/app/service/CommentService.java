package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.request.HideRequest;
import com.example.app.dto.response.comment.CommentResponse;
import com.example.app.dto.response.comment.CommentTreeResponse;
import com.example.app.exception.AppException;
import com.example.app.mapper.CommentMapper;
import com.example.app.model.Comment;
import com.example.app.model.Document;
import com.example.app.model.Lesson;
import com.example.app.model.User;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.share.GetUserByToken;
import com.example.app.share.SendNotification;
import com.example.app.share.Type;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final CommentMapper commentMapper;
	private final GetUserByToken getUserByToken;
	private final SendNotification sendNotification;

	@PreAuthorize("hasRole('ADMIN')")
	public CommentResponse hide(Long id, HideRequest dto) {
		Comment entity = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
		entity.setHide(dto.isHide());
		Comment saved = commentRepository.save(entity);
		return commentMapper.commentToCommentDocumentResponse(saved);
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

	@PreAuthorize("hasRole('HIDE_MY_COMMENT')")
	public CommentResponse hideMyComment(Long id) {
		User user = getUserByToken.get();

		Comment comment = commentRepository.findByIdAndUser_IdAndHideFalse(id, user.getId())
				.orElseThrow(() -> new RuntimeException("Không thấy comment"));

		comment.setHide(true);
		Comment saved = commentRepository.save(comment);
		return commentMapper.commentToCommentDocumentResponse(saved);
	}

	@PreAuthorize("hasRole('UPDATE_MY_COMMENT')")
	public CommentResponse updateMyComment(Long id, CommentRequest request) {
		User user = getUserByToken.get();

		Comment comment = commentRepository.findByIdAndUser_IdAndHideFalse(id, user.getId())
				.orElseThrow(() -> new RuntimeException("Không thấy comment"));

		commentMapper.updateComment(comment, request);
		comment.setUpdatedAt(LocalDateTime.now());

		Comment saved = commentRepository.save(comment);
		return commentMapper.commentToCommentDocumentResponse(saved);
	}

	@PreAuthorize("hasAuthority('POST_COMMENT')")
	public CommentResponse saveMyComment(CommentRequest dto) {
		Comment comment = commentMapper.commentRequestToComment(dto);

		User user = getUserByToken.get();

		if (comment.getType() == Type.DOCUMENT) {
			Document doc = documentRepository.findById(dto.getContentId())

					.orElseThrow(() -> new AppException("document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

			comment.setDocument(doc);
		} else {
			Lesson lesson = lessonRepository.findById(dto.getContentId())
					.orElseThrow(() -> new AppException("lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

			comment.setLesson(lesson);
		}

		comment.setUser(user);
		comment.setCreatedAt(LocalDateTime.now());

		CommentResponse response = saveComment(comment);
		sendNotification.sendNotificationCommentReply(dto.getIdParent(), user);
		return response;
	}

	public List<CommentTreeResponse> getDocumentCommentTree(Long docId) {
		List<Comment> comments = commentRepository.findByDocument_IdAndHideFalseOrderByLevelAscCreatedAtAsc(docId);
		Map<Long, CommentTreeResponse> map = mapToTreeDto(comments, Type.DOCUMENT);
		return buildTreeFromMap(map);
	}

	public List<CommentTreeResponse> getLessonCommentTree(Long lessonId) {
		List<Comment> comments = commentRepository.findByLesson_IdAndHideFalseOrderByLevelAscCreatedAtAsc(lessonId);
		Map<Long, CommentTreeResponse> map = mapToTreeDto(comments, Type.LESSON);
		return buildTreeFromMap(map);
	}

	private CommentResponse saveComment(Comment comment) {

		if (comment.getIdParent() == 0) {
			comment.setLevel(0L);
		} else {
			Comment parentComment = commentRepository.findById(comment.getIdParent())
					.orElseThrow(() -> new AppException("không tìm thấy comment", 1001, HttpStatus.BAD_REQUEST));
			comment.setLevel(parentComment.getLevel() + 1);
		}
		Comment saved = commentRepository.save(comment);

		return commentMapper.commentToCommentLessonResponse(saved);
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

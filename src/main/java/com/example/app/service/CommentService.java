package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.request.DisplayRequest;
import com.example.app.dto.response.comment.CommentResponse;
import com.example.app.dto.response.comment.CommentTreeResponse;
import com.example.app.event.DocumentCommentCreatedEvent;
import com.example.app.event.LessonCommentCreatedEvent;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.CommentMapper;
import com.example.app.model.Document;
import com.example.app.model.DocumentComment;
import com.example.app.model.Lesson;
import com.example.app.model.LessonComment;
import com.example.app.model.User;
import com.example.app.model.parent.BaseComment;
import com.example.app.repository.DocumentCommentRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonCommentRepository;
import com.example.app.repository.LessonRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {

	private final DocumentCommentRepository documentRepo;
	private final LessonCommentRepository lessonRepo;
	private final DocumentRepository documentRepository;
	private final LessonRepository lessonRepository;
	private final CommentMapper mapper;
	private final GetUserByToken getUser;
	private final ApplicationEventPublisher publisher;

	@PreAuthorize("hasAuthority('POST_COMMENT')")
	@Transactional
	public CommentResponse saveMyComment(CommentRequest req) {

		User user = getUser.get();

		return switch (req.getType()) {
		case DOCUMENT -> saveDocument(req, user);
		case LESSON -> saveLesson(req, user);
		default -> throw new AppException("Sai type", 1001, HttpStatus.BAD_REQUEST);
		};
	}

	private CommentResponse saveDocument(CommentRequest req, User user) {

		Document doc = documentRepository.findById(req.getContentId())
				.orElseThrow(() -> new AppException("Document không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		DocumentComment parent = getParentDocument(req.getParentId());

		DocumentComment comment = DocumentComment.builder().content(req.getContent()).user(user).document(doc)
				.parent(parent).level(calcLevel(parent)).createdAt(LocalDateTime.now()).hide(false).build();

		DocumentComment saved = documentRepo.save(comment);

		publishDocumentEvent(saved, parent);

		return mapper.documentCommentToCommentResponse(saved);
	}

	private CommentResponse saveLesson(CommentRequest req, User user) {

		Lesson lesson = lessonRepository.findById(req.getContentId())
				.orElseThrow(() -> new AppException("Lesson không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		LessonComment parent = getParentLesson(req.getParentId());

		LessonComment comment = LessonComment.builder().content(req.getContent()).user(user).lesson(lesson)
				.parent(parent).level(calcLevel(parent)).createdAt(LocalDateTime.now()).hide(false).build();

		LessonComment saved = lessonRepo.save(comment);

		publishLessonEvent(saved, parent);

		return mapper.lessonCommentToCommentResponse(saved);
	}

	@PreAuthorize("hasAuthority('UPDATE_MY_COMMENT')")
	public CommentResponse updateMyComment(Long id, CommentRequest req) {

		User user = getUser.get();

		return switch (req.getType()) {
		case DOCUMENT -> updateDocument(id, req, user);
		case LESSON -> updateLesson(id, req, user);
		default -> throw new AppException("Sai type", 1001, HttpStatus.BAD_REQUEST);
		};
	}

	private CommentResponse updateDocument(Long id, CommentRequest req, User user) {

		DocumentComment c = documentRepo.findByIdAndUser_IdAndHideFalse(id, user.getId())
				.orElseThrow(() -> new RuntimeException("Không thấy comment"));

		c.setContent(req.getContent());
		c.setUpdatedAt(LocalDateTime.now());

		return mapper.documentCommentToCommentResponse(documentRepo.save(c));
	}

	private CommentResponse updateLesson(Long id, CommentRequest req, User user) {

		LessonComment c = lessonRepo.findByIdAndUser_IdAndHideFalse(id, user.getId())
				.orElseThrow(() -> new RuntimeException("Không thấy comment"));

		c.setContent(req.getContent());
		c.setUpdatedAt(LocalDateTime.now());

		return mapper.lessonCommentToCommentResponse(lessonRepo.save(c));
	}

	// ================== HIDE ==================
	@PreAuthorize("hasRole('ADMIN')")
	public CommentResponse hide(Long id, DisplayRequest req) {

		return switch (req.getType()) {
		case DOCUMENT -> {
			DocumentComment c = documentRepo.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
			c.setHide(req.isHide());
			c.setUpdatedAt(LocalDateTime.now());
			yield mapper.documentCommentToCommentResponse(documentRepo.save(c));
		}
		case LESSON -> {
			LessonComment c = lessonRepo.findById(id).orElseThrow(() -> new RuntimeException("Không thấy comment"));
			c.setHide(req.isHide());
			c.setUpdatedAt(LocalDateTime.now());
			yield mapper.lessonCommentToCommentResponse(lessonRepo.save(c));
		}
		default -> throw new AppException("Sai type", 1001, HttpStatus.BAD_REQUEST);
		};
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<CommentResponse> getAllDocumentComments() {
		return documentRepo.findAll().stream().map(mapper::documentCommentToCommentResponse).toList();
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<CommentResponse> getAllLessonComments() {
		return lessonRepo.findAll().stream().map(mapper::lessonCommentToCommentResponse).toList();
	}

	public List<CommentTreeResponse> getDocumentTree(Long docId) {

		List<DocumentComment> list = documentRepo.findByDocument_IdAndHideFalseOrderByLevelAscCreatedAtAsc(docId);

		return buildTreeDocument(list);
	}

	public List<CommentTreeResponse> getLessonTree(Long lessonId) {

		List<LessonComment> list = lessonRepo.findByLesson_IdAndHideFalseOrderByLevelAscCreatedAtAsc(lessonId);

		return buildTreeLesson(list);
	}

	private List<CommentTreeResponse> buildTreeDocument(List<DocumentComment> list) {

		Map<Long, CommentTreeResponse> map = new HashMap<>();

		for (DocumentComment c : list) {
			CommentTreeResponse dto = mapper.documentCommentToCommentTreeResponse(c);
			map.put(dto.getId(), dto);
		}

		return buildTree(map);
	}

	private List<CommentTreeResponse> buildTreeLesson(List<LessonComment> list) {

		Map<Long, CommentTreeResponse> map = new HashMap<>();

		for (LessonComment c : list) {
			CommentTreeResponse dto = mapper.lessonCommentToCommentTreeResponse(c);
			map.put(dto.getId(), dto);
		}

		return buildTree(map);
	}

	private List<CommentTreeResponse> buildTree(Map<Long, CommentTreeResponse> map) {

		List<CommentTreeResponse> roots = new ArrayList<>();

		for (CommentTreeResponse dto : map.values()) {

			if (dto.getParentId() == null) {
				roots.add(dto);
			} else {
				CommentTreeResponse parent = map.get(dto.getParentId());
				if (parent != null) {
					parent.getChildren().add(dto);
				}
			}
		}

		return roots;
	}

	// ================== HELPER ==================
	private Long calcLevel(BaseComment parent) {
		return parent == null ? 0L : parent.getLevel() + 1;
	}

	private DocumentComment getParentDocument(Long parentId) {
		if (parentId == null)
			return null;
		return documentRepo.findById(parentId).orElseThrow(() -> new RuntimeException("Không tìm thấy parent"));
	}

	private LessonComment getParentLesson(Long parentId) {
		if (parentId == null)
			return null;
		return lessonRepo.findById(parentId).orElseThrow(() -> new RuntimeException("Không tìm thấy parent"));
	}

	private void publishDocumentEvent(DocumentComment saved, DocumentComment parent) {
		if (parent != null && !saved.getUser().equals(parent.getUser())) {
			publisher.publishEvent(new DocumentCommentCreatedEvent(saved, parent));
		}
	}

	private void publishLessonEvent(LessonComment saved, LessonComment parent) {
		if (parent != null && !saved.getUser().equals(parent.getUser())) {
			publisher.publishEvent(new LessonCommentCreatedEvent(saved, parent));
		}
	}
}
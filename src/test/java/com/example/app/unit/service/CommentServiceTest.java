package com.example.app.unit.service;

import com.example.app.constant.AppError;
import com.example.app.constant.InteractionType;
import com.example.app.dto.request.CommentRequest;
import com.example.app.dto.request.DisplayRequest;
import com.example.app.dto.response.comment.CommentResponse;
import com.example.app.dto.response.comment.CommentTreeResponse;
import com.example.app.event.DocumentCommentCreatedEvent;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.CommentMapper;
import com.example.app.model.*;
import com.example.app.repository.DocumentCommentRepository;
import com.example.app.repository.DocumentRepository;
import com.example.app.repository.LessonCommentRepository;
import com.example.app.repository.LessonRepository;
import com.example.app.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private DocumentCommentRepository documentRepo;

    @Mock
    private LessonCommentRepository lessonRepo;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CommentMapper mapper;

    @Mock
    private GetUserByToken getUser;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private CommentService commentService;

    @Test
    void saveMyComment_shouldSaveDocumentComment() {
        User user = User.builder().id(1L).username("user").build();
        Document document = Document.builder().id(10L).title("Doc").category(Category.builder().name("Cat").build()).build();
        CommentRequest req = CommentRequest.builder()
                .content("nice")
                .contentId(10L)
                .type(InteractionType.DOCUMENT)
                .build();
        DocumentComment saved = DocumentComment.builder()
                .id(100L)
                .content("nice")
                .user(user)
                .document(document)
                .level(0L)
                .hide(false)
                .createdAt(LocalDateTime.now())
                .build();
        CommentResponse response = CommentResponse.builder().id(100L).content("nice").build();

        when(getUser.get()).thenReturn(user);
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        when(documentRepo.save(any(DocumentComment.class))).thenReturn(saved);
        when(mapper.documentCommentToCommentResponse(saved)).thenReturn(response);

        CommentResponse actual = commentService.saveMyComment(req);

        assertEquals(response, actual);

        ArgumentCaptor<DocumentComment> captor = ArgumentCaptor.forClass(DocumentComment.class);
        verify(documentRepo).save(captor.capture());
        assertEquals("nice", captor.getValue().getContent());
        assertEquals(0L, captor.getValue().getLevel());
        assertEquals(user, captor.getValue().getUser());
        assertEquals(document, captor.getValue().getDocument());
    }

    @Test
    void saveMyComment_shouldPublishDocumentEventForReplyToAnotherUser() {
        User user = User.builder().id(1L).build();
        User parentUser = User.builder().id(2L).build();
        Document document = Document.builder().id(10L).build();
        DocumentComment parent = DocumentComment.builder().id(50L).user(parentUser).level(1L).build();
        DocumentComment saved = DocumentComment.builder().id(100L).content("reply").user(user).document(document)
                .parent(parent).level(2L).hide(false).createdAt(LocalDateTime.now()).build();
        CommentRequest req = CommentRequest.builder()
                .content("reply")
                .contentId(10L)
                .parentId(50L)
                .type(InteractionType.DOCUMENT)
                .build();

        when(getUser.get()).thenReturn(user);
        when(documentRepository.findById(10L)).thenReturn(Optional.of(document));
        when(documentRepo.findById(50L)).thenReturn(Optional.of(parent));
        when(documentRepo.save(any(DocumentComment.class))).thenReturn(saved);
        when(mapper.documentCommentToCommentResponse(saved)).thenReturn(CommentResponse.builder().id(100L).build());

        commentService.saveMyComment(req);

        verify(publisher).publishEvent(any(DocumentCommentCreatedEvent.class));
    }

    @Test
    void saveMyComment_shouldSaveLessonComment() {
        User user = User.builder().id(1L).username("user").build();
        Lesson lesson = Lesson.builder().id(10L).title("Lesson").build();
        CommentRequest req = CommentRequest.builder()
                .content("nice")
                .contentId(10L)
                .type(InteractionType.LESSON)
                .build();
        LessonComment saved = LessonComment.builder()
                .id(100L)
                .content("nice")
                .user(user)
                .lesson(lesson)
                .level(0L)
                .hide(false)
                .createdAt(LocalDateTime.now())
                .build();
        CommentResponse response = CommentResponse.builder().id(100L).content("nice").build();

        when(getUser.get()).thenReturn(user);
        when(lessonRepository.findById(10L)).thenReturn(Optional.of(lesson));
        when(lessonRepo.save(any(LessonComment.class))).thenReturn(saved);
        when(mapper.lessonCommentToCommentResponse(saved)).thenReturn(response);

        CommentResponse actual = commentService.saveMyComment(req);

        assertEquals(response, actual);
    }

    @Test
    void saveMyComment_shouldThrowWhenTypeInvalid() {
        User user = User.builder().id(1L).build();
        CommentRequest req = CommentRequest.builder()
                .content("nice")
                .contentId(10L)
                .type(null)
                .build();
        when(getUser.get()).thenReturn(user);

        assertThrows(NullPointerException.class, () -> commentService.saveMyComment(req));
    }

    @Test
    void saveMyComment_shouldThrowWhenDocumentNotFound() {
        User user = User.builder().id(1L).build();
        CommentRequest req = CommentRequest.builder()
                .content("nice")
                .contentId(10L)
                .type(InteractionType.DOCUMENT)
                .build();
        when(getUser.get()).thenReturn(user);
        when(documentRepository.findById(10L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> commentService.saveMyComment(req));

        assertEquals(AppError.DOCUMENT_NOT_FOUND, exception.getAppError());
    }

    @Test
    void updateMyComment_shouldUpdateDocumentComment() {
        User user = User.builder().id(1L).build();
        DocumentComment comment = DocumentComment.builder().id(100L).content("old").user(user).hide(false).build();
        CommentRequest req = CommentRequest.builder().content("new").type(InteractionType.DOCUMENT).build();
        CommentResponse response = CommentResponse.builder().id(100L).content("new").build();

        when(getUser.get()).thenReturn(user);
        when(documentRepo.findByIdAndUser_IdAndHideFalse(100L, 1L)).thenReturn(Optional.of(comment));
        when(documentRepo.save(any(DocumentComment.class))).thenReturn(comment);
        when(mapper.documentCommentToCommentResponse(comment)).thenReturn(response);

        CommentResponse actual = commentService.updateMyComment(100L, req);

        assertEquals(response, actual);
        assertEquals("new", comment.getContent());
    }

    @Test
    void updateMyComment_shouldThrowWhenCommentMissing() {
        User user = User.builder().id(1L).build();
        CommentRequest req = CommentRequest.builder().content("new").type(InteractionType.DOCUMENT).build();
        when(getUser.get()).thenReturn(user);
        when(documentRepo.findByIdAndUser_IdAndHideFalse(100L, 1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> commentService.updateMyComment(100L, req));

        assertEquals("Không thấy comment", exception.getMessage());
    }

    @Test
    void hide_shouldHideDocumentComment() {
        DocumentComment comment = DocumentComment.builder().id(100L).hide(false).build();
        DisplayRequest req = DisplayRequest.builder().hide(true).type(com.example.app.constant.HideType.DOCUMENT).build();
        CommentResponse response = CommentResponse.builder().id(100L).hide(true).build();

        when(documentRepo.findById(100L)).thenReturn(Optional.of(comment));
        when(documentRepo.save(any(DocumentComment.class))).thenReturn(comment);
        when(mapper.documentCommentToCommentResponse(comment)).thenReturn(response);

        CommentResponse actual = commentService.hide(100L, req);

        assertEquals(response, actual);
        assertEquals(true, comment.isHide());
    }

    @Test
    void getAllDocumentComments_shouldMapAll() {
        DocumentComment c1 = DocumentComment.builder().id(1L).build();
        DocumentComment c2 = DocumentComment.builder().id(2L).build();
        CommentResponse r1 = CommentResponse.builder().id(1L).build();
        CommentResponse r2 = CommentResponse.builder().id(2L).build();

        when(documentRepo.findAll()).thenReturn(List.of(c1, c2));
        when(mapper.documentCommentToCommentResponse(c1)).thenReturn(r1);
        when(mapper.documentCommentToCommentResponse(c2)).thenReturn(r2);

        assertEquals(List.of(r1, r2), commentService.getAllDocumentComments());
    }

    @Test
    void getDocumentTree_shouldBuildNestedTree() {
        DocumentComment root = DocumentComment.builder().id(1L).build();
        DocumentComment child = DocumentComment.builder().id(2L).build();
        CommentTreeResponse rootDto = CommentTreeResponse.builder().id(1L).parentId(null).build();
        CommentTreeResponse childDto = CommentTreeResponse.builder().id(2L).parentId(1L).build();

        when(documentRepo.findByDocument_IdAndHideFalseOrderByLevelAscCreatedAtAsc(10L)).thenReturn(List.of(root, child));
        when(mapper.documentCommentToCommentTreeResponse(root)).thenReturn(rootDto);
        when(mapper.documentCommentToCommentTreeResponse(child)).thenReturn(childDto);

        List<CommentTreeResponse> result = commentService.getDocumentTree(10L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals(2L, result.get(0).getChildren().get(0).getId());
    }

    @Test
    void getLessonTree_shouldBuildNestedTree() {
        LessonComment root = LessonComment.builder().id(1L).build();
        LessonComment child = LessonComment.builder().id(2L).build();
        CommentTreeResponse rootDto = CommentTreeResponse.builder().id(1L).parentId(null).build();
        CommentTreeResponse childDto = CommentTreeResponse.builder().id(2L).parentId(1L).build();

        when(lessonRepo.findByLesson_IdAndHideFalseOrderByLevelAscCreatedAtAsc(10L)).thenReturn(List.of(root, child));
        when(mapper.lessonCommentToCommentTreeResponse(root)).thenReturn(rootDto);
        when(mapper.lessonCommentToCommentTreeResponse(child)).thenReturn(childDto);

        List<CommentTreeResponse> result = commentService.getLessonTree(10L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1, result.get(0).getChildren().size());
    }

    @Test
    void hide_shouldThrowWhenTypeInvalid() {
        DisplayRequest req = DisplayRequest.builder().hide(true).type(com.example.app.constant.HideType.USER).build();

        AppException exception = assertThrows(AppException.class, () -> commentService.hide(100L, req));

        assertEquals(AppError.TYPE_NOT_FOUND, exception.getAppError());
    }
}

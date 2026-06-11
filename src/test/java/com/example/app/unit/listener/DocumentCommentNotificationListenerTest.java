package com.example.app.unit.listener;

import com.example.app.constant.NotificationType;
import com.example.app.event.DocumentCommentCreatedEvent;
import com.example.app.listener.DocumentCommentNotificationListener;
import com.example.app.model.Document;
import com.example.app.model.DocumentComment;
import com.example.app.model.Notification;
import com.example.app.model.User;
import com.example.app.service.NotificationService;
import com.example.app.service.UserNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentCommentNotificationListener Tests")
class DocumentCommentNotificationListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserNotificationService userNotificationService;

    @InjectMocks
    private DocumentCommentNotificationListener listener;

    private User commenter;
    private User commentOwner;
    private Document document;
    private DocumentComment childComment;
    private DocumentComment parentComment;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "frontendDomain", "http://localhost:3000");

        commenter = User.builder().id(1L).username("commenter").build();
        commentOwner = User.builder().id(2L).username("commentOwner").build();
        document = Document.builder().id(1L).title("Test Document").build();

        parentComment = DocumentComment.builder().id(1L).user(commentOwner).document(document)
                .content("Parent comment").build();

        childComment = DocumentComment.builder().id(2L).user(commenter).document(document)
                .content("Child comment").parent(parentComment).build();

        testNotification = Notification.builder().id(1L).content("notif").link("http://").type(NotificationType.INFO).build();

        lenient().when(notificationService.saveNotification(any())).thenReturn(testNotification);
        lenient().when(userNotificationService.saveUserNotification(any())).thenReturn(true);
        lenient().doNothing().when(userNotificationService).sendToFollower(any(), anyLong(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle document comment created event")
    void testHandleDocumentCommentCreatedEvent() {
        DocumentCommentCreatedEvent event = new DocumentCommentCreatedEvent(childComment, parentComment);

        listener.handle(event);

        verify(notificationService, times(1)).saveNotification(any());
        verify(userNotificationService, times(1)).saveUserNotification(any());
    }

    @Test
    @DisplayName("Should create correct notification content and type")
    void testNotificationContentAndType() {
        DocumentCommentCreatedEvent event = new DocumentCommentCreatedEvent(childComment, parentComment);

        listener.handle(event);

        verify(notificationService, atLeastOnce()).saveNotification(argThat(req ->
                req.getContent() != null && req.getType() == NotificationType.INFO
        ));
    }
}


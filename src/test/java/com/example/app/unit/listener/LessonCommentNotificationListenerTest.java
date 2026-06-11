package com.example.app.unit.listener;

import com.example.app.constant.NotificationType;
import com.example.app.event.LessonCommentCreatedEvent;
import com.example.app.listener.LessonCommentNotificationListener;
import com.example.app.model.Lesson;
import com.example.app.model.LessonComment;
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
@DisplayName("LessonCommentNotificationListener Tests")
class LessonCommentNotificationListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserNotificationService userNotificationService;

    @InjectMocks
    private LessonCommentNotificationListener listener;

    private User commenter;
    private User commentOwner;
    private Lesson lesson;
    private LessonComment childComment;
    private LessonComment parentComment;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "frontendDomain", "http://localhost:3000");

        commenter = User.builder().id(1L).username("commenter").build();
        commentOwner = User.builder().id(2L).username("commentOwner").build();
        lesson = Lesson.builder().id(1L).title("Test Lesson").user(commentOwner).build();

        parentComment = LessonComment.builder().id(1L).user(commentOwner).lesson(lesson).content("Parent").build();
        childComment = LessonComment.builder().id(2L).user(commenter).lesson(lesson).content("Child").parent(parentComment).build();

        testNotification = Notification.builder().id(1L).content("notif").link("http://").type(NotificationType.INFO).build();

        lenient().when(notificationService.saveNotification(any())).thenReturn(testNotification);
        lenient().when(userNotificationService.saveUserNotification(any())).thenReturn(true);
        lenient().doNothing().when(userNotificationService).sendToFollower(any(), anyLong(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle lesson comment created event")
    void testHandleLessonCommentCreatedEvent() {
        LessonCommentCreatedEvent event = new LessonCommentCreatedEvent(childComment, parentComment);

        listener.handle(event);

        verify(notificationService, times(1)).saveNotification(any());
        verify(userNotificationService, times(1)).saveUserNotification(any());
    }
}


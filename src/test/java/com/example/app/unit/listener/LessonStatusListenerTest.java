package com.example.app.unit.listener;

import com.example.app.constant.ContentStatus;
import com.example.app.constant.NotificationAction;
import com.example.app.dto.response.lesson.LessonEventDTO;
import com.example.app.event.LessonStatusEvent;
import com.example.app.listener.LessonStatusListener;
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
@DisplayName("LessonStatusListener Tests")
class LessonStatusListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserNotificationService userNotificationService;

    @InjectMocks
    private LessonStatusListener listener;

    private User admin;
    private User author;
    private LessonEventDTO lesson;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "frontendDomain", "http://localhost:3000");

        admin = User.builder().id(1L).username("admin").build();
        author = User.builder().id(2L).username("author").build();

        lesson = LessonEventDTO.builder().id(1L).title("L").user(author).status(ContentStatus.PUBLISHED)
                .build();

        lenient().when(notificationService.saveNotification(any())).thenReturn(null);
        lenient().when(userNotificationService.saveUserNotification(any())).thenReturn(true);
        lenient().doNothing().when(userNotificationService).sendToFollower(any(), anyLong(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle PUBLIC action")
    void testHandlePublicAction() {
        LessonStatusEvent event = new LessonStatusEvent(lesson, admin, NotificationAction.PUBLIC);

        listener.handle(event);

        verify(notificationService, atLeastOnce()).saveNotification(any());
        verify(userNotificationService, atLeastOnce()).sendToFollower(any(), anyLong(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle ADMIN_HIDDEN action")
    void testHandleAdminHiddenAction() {
        LessonStatusEvent event = new LessonStatusEvent(lesson, admin, NotificationAction.ADMIN_HIDDEN);

        listener.handle(event);

        verify(notificationService, atLeastOnce()).saveNotification(any());
    }
}


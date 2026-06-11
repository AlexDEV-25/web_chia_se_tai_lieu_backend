package com.example.app.unit.listener;

import com.example.app.constant.NotificationType;
import com.example.app.event.UserFollowCreateEvent;
import com.example.app.listener.UserFollowCreateListener;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserFollowCreateListener Tests")
class UserFollowCreateListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserNotificationService userNotificationService;

    @InjectMocks
    private UserFollowCreateListener listener;

    private User follower;
    private User following;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "frontendDomain", "http://localhost:3000");

        follower = User.builder().id(1L).username("follower").build();
        following = User.builder().id(2L).username("following").build();

        testNotification = Notification.builder().id(1L).content("notif").link("http://").type(NotificationType.INFO).build();
        when(notificationService.saveNotification(any())).thenReturn(testNotification);
        when(userNotificationService.saveUserNotification(any())).thenReturn(true);
    }

    @Test
    @DisplayName("Should handle user follow event and send notification")
    void testHandleUserFollowCreateEvent() {
        UserFollowCreateEvent event = new UserFollowCreateEvent(follower, following);

        listener.handle(event);

        verify(notificationService, times(1)).saveNotification(any());
        verify(userNotificationService, times(1)).saveUserNotification(any());
    }
}


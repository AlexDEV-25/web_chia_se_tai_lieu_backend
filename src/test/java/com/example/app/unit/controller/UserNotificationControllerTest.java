package com.example.app.unit.controller;

import com.example.app.constant.NotificationType;
import com.example.app.dto.response.usernotificaion.UserNotificationResponse;
import com.example.app.service.UserNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("UserNotificationController Tests")
class UserNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserNotificationService userNotificationService;

    private UserNotificationResponse userNotificationResponse;

    @BeforeEach
    void setUp() {
        userNotificationResponse = UserNotificationResponse.builder().id(1L).senderId(2L).senderName("testuser")
                .receiverId(1L).receiverName("currentuser").notificationId(1L)
                .notificationContent("This is a test notification").notificationLink("/documents/1")
                .notificationType(NotificationType.INFO).read(false).createdAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("GET /api/user-notifications/receiver - Should get user notifications")
    @WithMockUser(authorities = "GET_ALL_USER_NOTIFICATION")
    void testGetByReceiver_Success() throws Exception {
        List<UserNotificationResponse> notifications = new ArrayList<>();
        notifications.add(userNotificationResponse);

        when(userNotificationService.getByReceiver()).thenReturn(notifications);

        mockMvc.perform(get("/api/user-notifications/receiver")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList[0].notificationContent").value("This is a test notification"))
                .andExpect(jsonPath("$.resultList[0].read").value(false));
    }

    @Test
    @DisplayName("GET /api/user-notifications/receiver/unread - Should get unread notifications")
    @WithMockUser(authorities = "GET_UNREAD_USER_NOTIFICATION")
    void testGetByReceiverIdAndReadFalse_Success() throws Exception {
        List<UserNotificationResponse> unreadNotifications = new ArrayList<>();
        unreadNotifications.add(userNotificationResponse);

        when(userNotificationService.getByReceiverIdAndReadFalse()).thenReturn(unreadNotifications);

        mockMvc.perform(get("/api/user-notifications/receiver/unread")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList[0].read").value(false));
    }

    @Test
    @DisplayName("PUT /api/user-notifications/read/{id} - Should mark notification as read")
    @WithMockUser(authorities = "READ_NOTIFICATION")
    void testRead_Success() throws Exception {
        UserNotificationResponse readNotification = UserNotificationResponse.builder().id(1L).senderId(2L)
                .senderName("testuser").receiverId(1L).receiverName("currentuser").notificationId(1L)
                .notificationContent("This is a test notification").notificationLink("/documents/1")
                .notificationType(NotificationType.INFO).read(true).createdAt(LocalDateTime.now()).build();

        when(userNotificationService.read(anyLong())).thenReturn(readNotification);

        mockMvc.perform(put("/api/user-notifications/read/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.read").value(true));
    }

    @Test
    @DisplayName("PUT /api/user-notifications/read-all/{id} - Should mark all notifications as read")
    @WithMockUser(authorities = "READ_ALL_NOTIFICATION")
    void testReadAll_Success() throws Exception {
        doNothing().when(userNotificationService).readAll(anyLong());

        mockMvc.perform(put("/api/user-notifications/read-all/1")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/user-notifications/receiver - Should return empty list when no notifications")
    @WithMockUser(authorities = "GET_ALL_USER_NOTIFICATION")
    void testGetByReceiver_Empty() throws Exception {
        List<UserNotificationResponse> notifications = new ArrayList<>();

        when(userNotificationService.getByReceiver()).thenReturn(notifications);

        mockMvc.perform(get("/api/user-notifications/receiver")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList").isArray()).andExpect(jsonPath("$.resultList.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/user-notifications/receiver/unread - Should return empty list when no unread")
    @WithMockUser(authorities = "GET_UNREAD_USER_NOTIFICATION")
    void testGetByReceiverIdAndReadFalse_Empty() throws Exception {
        List<UserNotificationResponse> unreadNotifications = new ArrayList<>();

        when(userNotificationService.getByReceiverIdAndReadFalse()).thenReturn(unreadNotifications);

        mockMvc.perform(get("/api/user-notifications/receiver/unread")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList").isArray()).andExpect(jsonPath("$.resultList.length()").value(0));
    }
}

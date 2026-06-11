package com.example.app.unit.controller;

import com.example.app.constant.ChatRole;
import com.example.app.dto.request.ParticipantInfoRequest;
import com.example.app.dto.response.participantinfo.ParticipantInfoResponse;
import com.example.app.service.ParticipantInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("ParticipantInfoController Tests")
class ParticipantInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantInfoService participantInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ParticipantInfoResponse participantInfoResponse;
    private ParticipantInfoRequest participantInfoRequest;

    @BeforeEach
    void setUp() {
        participantInfoResponse = ParticipantInfoResponse.builder().id(1L).userId(1L).userName("testuser")
                .chatRole(ChatRole.MEMBER).build();

        participantInfoRequest = ParticipantInfoRequest.builder().userId(2L).conversationId(1L)
                .chatRole(ChatRole.MEMBER).build();
    }

    @Test
    @DisplayName("PUT /api/participant-infos/update-last-seen/{id} - Should update last seen")
    @WithMockUser(authorities = "UPDATE_LAST_SEEN")
    void testUpdateLastSeen_Success() throws Exception {
        when(participantInfoService.updateLastSeen(anyLong())).thenReturn(participantInfoResponse);

        mockMvc.perform(put("/api/participant-infos/update-last-seen/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1L));
    }

    @Test
    @DisplayName("POST /api/participant-infos/add-member - Should add member to conversation")
    @WithMockUser(authorities = "ADD_MEMBER")
    void testAddMember_Success() throws Exception {
        when(participantInfoService.addMember(any(ParticipantInfoRequest.class))).thenReturn(participantInfoResponse);

        mockMvc.perform(post("/api/participant-infos/add-member").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participantInfoRequest))).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.userId").value(1L));
    }

    @Test
    @DisplayName("DELETE /api/participant-infos/delete-member/user-id/{userId}/conversation-id/{conversationId} - Should delete member")
    @WithMockUser(authorities = "DELETE_MEMBER")
    void testDeleteMember_Success() throws Exception {
        doNothing().when(participantInfoService).deleteMember(anyLong(), anyLong());

        mockMvc.perform(delete("/api/participant-infos/delete-member/user-id/1/conversation-id/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/participant-infos/change-role - Should change member role")
    @WithMockUser(authorities = "CHANGE_ROLE")
    void testChangeRole_Success() throws Exception {
        ParticipantInfoRequest changeRoleRequest = ParticipantInfoRequest.builder().userId(1L).conversationId(1L)
                .chatRole(ChatRole.MANAGER).build();

        ParticipantInfoResponse updatedResponse = ParticipantInfoResponse.builder().id(1L).userId(1L)
                .userName("testuser").chatRole(ChatRole.DEPUTYMANAGER).build();

        when(participantInfoService.changeRole(any(ParticipantInfoRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/participant-infos/change-role").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRoleRequest))).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.chatRole").value(ChatRole.DEPUTYMANAGER.name()));
    }

    @Test
    @DisplayName("POST /api/participant-infos/add-member - Should handle validation errors")
    @WithMockUser(authorities = "ADD_MEMBER")
    void testAddMember_InvalidRequest() throws Exception {
        ParticipantInfoRequest invalidRequest = ParticipantInfoRequest.builder().userId(null) // Invalid: missing user
                // id
                .conversationId(1L).chatRole(ChatRole.MEMBER).build();

        mockMvc.perform(post("/api/participant-infos/add-member").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))).andExpect(status().isBadRequest());
    }
}

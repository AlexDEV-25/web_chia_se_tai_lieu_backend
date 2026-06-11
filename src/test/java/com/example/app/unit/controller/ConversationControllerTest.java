package com.example.app.unit.controller;

import com.example.app.constant.ConversationType;
import com.example.app.dto.request.ConversationGroupRequest;
import com.example.app.dto.request.ConversationRequest;
import com.example.app.dto.response.conversation.ConversationResponse;
import com.example.app.service.ConversationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@DisplayName("ConversationController Tests")
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ConversationResponse conversationResponse;
    private ConversationRequest conversationRequest;

    @BeforeEach
    void setUp() {
        conversationResponse = ConversationResponse.builder().id(1L).type(ConversationType.DIRECT)
                .conversationName("Test Conversation").conversationAvatar("http://example.com/avatar.jpg")
                .participantInfos(new ArrayList<>()).build();

        conversationRequest = ConversationRequest.builder().type(ConversationType.DIRECT)
                .participantIds(Arrays.asList(2L)).build();
    }

    @Test
    @DisplayName("POST /api/conversations/direct - Should create direct conversation")
    @WithMockUser(authorities = "CREATE_DIRECT_CONVERSATION")
    void testCreateDirectConversation_Success() throws Exception {
        when(conversationService.createDirectConversation(any(ConversationRequest.class)))
                .thenReturn(conversationResponse);

        mockMvc.perform(post("/api/conversations/direct").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conversationRequest))).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.type").value(ConversationType.DIRECT.name()));
    }

    @Test
    @DisplayName("POST /api/conversations/group - Should create group conversation")
    @WithMockUser(authorities = "CREATE_GROUP_CONVERSATION")
    void testCreateGroupConversation_Success() throws Exception {

        ConversationGroupRequest groupRequest = ConversationGroupRequest.builder().type(ConversationType.GROUP)
                .participantIds(Arrays.asList(2L)).groupName("Group Name").build();

        ConversationResponse groupResponse = ConversationResponse.builder().id(2L).type(ConversationType.GROUP)
                .conversationName("Group Name").conversationAvatar("http://example.com/avatar.jpg")
                .participantInfos(new ArrayList<>()).build();

        when(conversationService.createGroupConversation(any(), any())).thenReturn(groupResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(groupRequest);

        MockMultipartFile data = new MockMultipartFile("data", "", "application/json", jsonData.getBytes());

        mockMvc.perform(multipart("/api/conversations/group").file(data).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk()).andExpect(jsonPath("$.result.type").value(ConversationType.GROUP.name()));

    }

    @Test
    @DisplayName("GET /api/conversations/my-conversations - Should get user conversations")
    @WithMockUser(authorities = "GET_MY_CONVERSATION")
    void testGetMyConversation_Success() throws Exception {
        List<ConversationResponse> conversations = new ArrayList<>();
        conversations.add(conversationResponse);

        when(conversationService.getMyConversations()).thenReturn(conversations);

        mockMvc.perform(get("/api/conversations/my-conversations")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList[0].conversationName").value("Test Conversation"));
    }

    @Test
    @DisplayName("GET /api/conversations/search - Should search conversations")
    @WithMockUser(authorities = "SEARCH_CONVERSATION")
    void testSearch_Success() throws Exception {
        List<ConversationResponse> searchResults = new ArrayList<>();
        searchResults.add(conversationResponse);

        when(conversationService.search(anyString())).thenReturn(searchResults);

        mockMvc.perform(get("/api/conversations/search").param("keyword", "test")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList[0].conversationName").value("Test Conversation"));
    }

    @Test
    @DisplayName("GET /api/conversations/detail/{id} - Should get conversation detail")
    @WithMockUser(authorities = "GET_DETAIL_CONVERSATION")
    void testGetDetailConversation_Success() throws Exception {
        when(conversationService.getDetailConversation(anyLong())).thenReturn(conversationResponse);

        mockMvc.perform(get("/api/conversations/detail/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.result.conversationName").value("Test Conversation"));
    }

    @Test
    @DisplayName("GET /api/conversations/my-conversations - Should return empty list when no conversations")
    @WithMockUser(authorities = "GET_MY_CONVERSATION")
    void testGetMyConversation_Empty() throws Exception {
        List<ConversationResponse> conversations = new ArrayList<>();

        when(conversationService.getMyConversations()).thenReturn(conversations);

        mockMvc.perform(get("/api/conversations/my-conversations")).andExpect(status().isOk())
                .andExpect(jsonPath("$.resultList").isArray()).andExpect(jsonPath("$.resultList.length()").value(0));
    }
}

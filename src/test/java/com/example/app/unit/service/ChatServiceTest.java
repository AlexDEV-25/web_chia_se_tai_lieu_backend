package com.example.app.unit.service;

import com.example.app.constant.AppError;
import com.example.app.constant.InteractionType;
import com.example.app.dto.response.ai.ChatHistoryResponse;
import com.example.app.dto.response.comment.CommentResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.CommentMapper;
import com.example.app.model.Category;
import com.example.app.model.DocumentComment;
import com.example.app.model.User;
import com.example.app.repository.*;
import com.example.app.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.*;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClientRequestSpec requestSpec;

    @Mock
    private CallResponseSpec callResponseSpec;

    @Mock
    private AdvisorSpec advisorSpec;

    @Mock
    private PromptUserSpec promptUserSpec;

    @Mock
    private PromptSystemSpec promptSystemSpec;

    @Mock
    private JdbcChatMemoryRepository jdbcChatMemoryRepository;

    @Mock
    private GetUserByToken getUserByToken;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private DocumentCommentRepository commentDocumentRepository;

    @Mock
    private LessonCommentRepository commentLessonRepository;

    @Mock
    private DocumentRatingRepository ratingDocumentRepository;

    @Mock
    private LessonRatingRepository ratingLessonRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private CommentMapper commentMapper;

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.defaultAdvisors(any(org.springframework.ai.chat.client.advisor.api.Advisor[].class)))
                .thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        chatService = new ChatService(chatClientBuilder, jdbcChatMemoryRepository, getUserByToken, categoryRepository,
                lessonRepository, commentDocumentRepository, commentLessonRepository, ratingDocumentRepository,
                ratingLessonRepository, documentRepository, commentMapper);
    }

    @Test
    void filterComment_shouldReturnFilteredCommentsForDocuments() {
        CommentResponse commentResponse = CommentResponse.builder().id(1L).content("bad").build();
        DocumentComment documentComment = DocumentComment.builder().build();

        when(commentDocumentRepository.findDocumentCommentsLast7Days(any())).thenReturn(List.of(documentComment));
        when(commentMapper.documentCommentToCommentResponse(documentComment)).thenReturn(commentResponse);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.advisors((Consumer<AdvisorSpec>) any())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(any(ParameterizedTypeReference.class))).thenReturn(List.of(commentResponse));

        List<CommentResponse> result = chatService.filterCommnent(InteractionType.DOCUMENT.name());

        assertEquals(List.of(commentResponse), result);
        verify(commentDocumentRepository).findDocumentCommentsLast7Days(any());
    }

    @Test
    void filterComment_shouldReturnEmptyListWhenNoComments() {
        when(commentLessonRepository.findLessonCommentsLast7Days(any())).thenReturn(List.of());

        List<CommentResponse> result = chatService.filterCommnent(InteractionType.LESSON.name());

        assertEquals(List.of(), result);
        verifyNoInteractions(chatClient);
    }

    @Test
    void chat_shouldUseQaPromptWithoutFile() {
        User me = User.builder().id(1L).build();
        when(getUserByToken.get()).thenReturn(me);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.advisors((Consumer<AdvisorSpec>) any())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("QA", "final answer");
        when(categoryRepository.findByHideFalse()).thenReturn(List.of(Category.builder().name("Java").build()));

        String result = chatService.chat(null, "What is Java?");

        assertEquals("final answer", result);
    }

    @Test
    void chat_shouldUseFileBranch() {
        User me = User.builder().id(1L).build();
        MockMultipartFile file = new MockMultipartFile("file", "sample.txt", "text/plain", "hello".getBytes());
        when(getUserByToken.get()).thenReturn(me);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(any(java.util.function.Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.options(any(ChatOptions.class))).thenReturn(requestSpec);
        when(requestSpec.advisors((Consumer<AdvisorSpec>) any())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("QA", "file answer");
        when(categoryRepository.findByHideFalse()).thenReturn(List.of(Category.builder().name("Java").build()));

        String result = chatService.chat(file, "Explain this file");

        assertEquals("file answer", result);
    }

    @Test
    void chat_shouldThrowWhenNoCategoriesForQa() {
        User me = User.builder().id(1L).build();
        when(getUserByToken.get()).thenReturn(me);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("QA");
        when(categoryRepository.findByHideFalse()).thenReturn(List.of());

        AppException exception = assertThrows(AppException.class, () -> chatService.chat(null, "What is Java?"));

        assertEquals(AppError.CATEGORY_NOT_FOUND, exception.getAppError());
    }

    @Test
    void getChatHistory_shouldFilterOnlyUserAndAssistantMessages() {
        User me = User.builder().id(1L).build();
        Message userMessage = org.mockito.Mockito.mock(Message.class);
        Message assistantMessage = org.mockito.Mockito.mock(Message.class);
        Message systemMessage = org.mockito.Mockito.mock(Message.class);

        when(getUserByToken.get()).thenReturn(me);
        when(jdbcChatMemoryRepository.findByConversationId("1")).thenReturn(List.of(userMessage, assistantMessage, systemMessage));
        when(userMessage.getMessageType()).thenReturn(MessageType.USER);
        when(userMessage.getText()).thenReturn("hello");
        when(assistantMessage.getMessageType()).thenReturn(MessageType.ASSISTANT);
        when(assistantMessage.getText()).thenReturn("hi");
        when(systemMessage.getMessageType()).thenReturn(MessageType.SYSTEM);

        List<ChatHistoryResponse> history = chatService.getChatHistory();

        assertEquals(2, history.size());
        assertEquals("USER", history.get(0).getRole());
        assertEquals("hello", history.get(0).getContent());
        assertEquals("ASSISTANT", history.get(1).getRole());
        assertEquals("hi", history.get(1).getContent());
    }

}

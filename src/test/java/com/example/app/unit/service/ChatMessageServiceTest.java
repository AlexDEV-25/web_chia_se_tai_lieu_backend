package com.example.app.unit.service;

import com.example.app.constant.AppError;
import com.example.app.constant.ConnectionStatus;
import com.example.app.constant.ConversationType;
import com.example.app.dto.request.ChatMessageRequest;
import com.example.app.dto.response.chatmessage.ChatMessageResponse;
import com.example.app.event.MessageCreatedEvent;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.ChatMessageMapper;
import com.example.app.model.ChatMessage;
import com.example.app.model.Conversation;
import com.example.app.model.User;
import com.example.app.repository.ChatMessageRepository;
import com.example.app.repository.ConversationRepository;
import com.example.app.repository.ParticipantInfoRepository;
import com.example.app.service.ChatMessageService;
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
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ParticipantInfoRepository participantInfoRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private ChatMessageMapper chatMessageMapper;

    @Mock
    private GetUserByToken getUserByToken;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Test
    void getMessages_shouldReturnMappedMessages() {
        User me = User.builder().id(1L).username("me").build();
        User sender = User.builder().id(2L).username("sender").status(ConnectionStatus.ONLINE).build();
        Conversation conversation = Conversation.builder().id(10L).type(ConversationType.DIRECT).build();
        ChatMessage message1 = ChatMessage.builder().id(100L).conversation(conversation).sender(sender)
                .message("hello").createdAt(LocalDateTime.now()).build();
        ChatMessage message2 = ChatMessage.builder().id(101L).conversation(conversation).sender(me)
                .message("hi").createdAt(LocalDateTime.now()).build();
        ChatMessageResponse response1 = ChatMessageResponse.builder().id(100L).message("hello").userId(2L)
                .userName("sender").userStatus(ConnectionStatus.ONLINE).build();
        ChatMessageResponse response2 = ChatMessageResponse.builder().id(101L).message("hi").userId(1L)
                .userName("me").userStatus(null).build();

        when(getUserByToken.get()).thenReturn(me);
        when(chatMessageRepository.findAllByConversation_IdOrderByCreatedAtAsc(10L)).thenReturn(List.of(message1, message2));
        when(chatMessageMapper.chatMessagetoChatMessageResponse(message1)).thenReturn(response1);
        when(chatMessageMapper.chatMessagetoChatMessageResponse(message2)).thenReturn(response2);

        List<ChatMessageResponse> responses = chatMessageService.getMessages(10L);

        assertEquals(List.of(response1, response2), responses);
        assertEquals(false, response1.isMe());
        assertEquals(true, response2.isMe());
    }

    @Test
    void getMessages_shouldThrowWhenUserNotFound() {
        when(getUserByToken.get()).thenReturn(null);

        AppException exception = assertThrows(AppException.class, () -> chatMessageService.getMessages(10L));

        assertEquals(AppError.USER_NOT_FOUND, exception.getAppError());
    }

    @Test
    void createMessage_shouldSaveAndPublishEvent() {
        User me = User.builder().id(1L).username("me").avatarUrl("avatar").status(ConnectionStatus.ONLINE).build();
        Conversation conversation = Conversation.builder().id(10L).type(ConversationType.DIRECT).build();
        ChatMessageRequest request = ChatMessageRequest.builder().conversationId(10L).message("hello").build();
        ChatMessage saved = ChatMessage.builder().id(100L).conversation(conversation).sender(me).message("hello")
                .createdAt(LocalDateTime.now()).build();
        ChatMessageResponse response = ChatMessageResponse.builder().id(100L).conversationId(10L).message("hello")
                .userId(1L).userName("me").userAvatar("avatar").userStatus(ConnectionStatus.ONLINE).build();

        when(getUserByToken.get()).thenReturn(me);
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));
        when(participantInfoRepository.existsByConversation_IdAndUser_Id(10L, 1L)).thenReturn(true);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(saved);
        when(chatMessageMapper.chatMessagetoChatMessageResponse(saved)).thenReturn(response);

        ChatMessageResponse actual = chatMessageService.createMessage(request);

        assertEquals(response, actual);
        assertEquals(true, actual.isMe());

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());
        assertEquals("hello", captor.getValue().getMessage());
        assertEquals(conversation, captor.getValue().getConversation());
        assertEquals(me, captor.getValue().getSender());
        verify(publisher).publishEvent(any(MessageCreatedEvent.class));
    }

    @Test
    void createMessage_shouldThrowWhenUserNotFound() {
        ChatMessageRequest request = ChatMessageRequest.builder().conversationId(10L).message("hello").build();
        when(getUserByToken.get()).thenReturn(null);

        AppException exception = assertThrows(AppException.class, () -> chatMessageService.createMessage(request));

        assertEquals(AppError.USER_NOT_FOUND, exception.getAppError());
    }

    @Test
    void createMessage_shouldThrowWhenConversationNotFound() {
        User me = User.builder().id(1L).username("me").build();
        ChatMessageRequest request = ChatMessageRequest.builder().conversationId(10L).message("hello").build();

        when(getUserByToken.get()).thenReturn(me);
        when(conversationRepository.findById(10L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> chatMessageService.createMessage(request));

        assertEquals(AppError.CONVERSATION_NOT_FOUND, exception.getAppError());
    }

    @Test
    void createMessage_shouldThrowWhenNotParticipant() {
        User me = User.builder().id(1L).username("me").build();
        Conversation conversation = Conversation.builder().id(10L).type(ConversationType.DIRECT).build();
        ChatMessageRequest request = ChatMessageRequest.builder().conversationId(10L).message("hello").build();

        when(getUserByToken.get()).thenReturn(me);
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(conversation));
        when(participantInfoRepository.existsByConversation_IdAndUser_Id(10L, 1L)).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> chatMessageService.createMessage(request));

        assertEquals(AppError.NOT_CONVERSATION_MEMBER, exception.getAppError());
    }
}

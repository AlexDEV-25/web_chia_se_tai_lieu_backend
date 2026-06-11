package com.example.app.unit.listener;

import com.example.app.dto.response.chatmessage.ChatMessageResponse;
import com.example.app.event.MessageCreatedEvent;
import com.example.app.listener.MessageCreatedListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageCreatedListener Tests")
class MessageCreatedListenerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageCreatedListener listener;

    @Test
    @DisplayName("Should send message to websocket topic on message created event")
    void testHandleMessageCreated() {
        ChatMessageResponse msg =
                com.example.app.dto.response.chatmessage.ChatMessageResponse.builder()
                        .conversationId(123L).message("hi").build();
        MessageCreatedEvent event = new MessageCreatedEvent(msg);

        listener.handleMessageCreated(event);

        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/conversation/" + msg.getConversationId()), eq(msg));
    }
}



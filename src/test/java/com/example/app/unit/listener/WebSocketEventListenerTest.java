package com.example.app.unit.listener;

import com.example.app.constant.ConnectionStatus;
import com.example.app.listener.WebSocketEventListener;
import com.example.app.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocketEventListener Tests")
class WebSocketEventListenerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private WebSocketEventListener listener;

    @Test
    @DisplayName("Should set user online on connect event")
    void testHandleConnect() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        Principal p = () -> "testuser";
        accessor.setUser(p);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        SessionConnectEvent event = new SessionConnectEvent(this, message);

        listener.handleConnect(event);

        verify(userService, times(1)).changeConnectStatus("testuser", ConnectionStatus.ONLINE);
    }

    @Test
    @DisplayName("Should set user offline on disconnect event")
    void testHandleDisconnect() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
        Principal p = () -> "testuser";
        accessor.setUser(p);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        SessionDisconnectEvent event = new SessionDisconnectEvent(this, message, "s1", CloseStatus.NORMAL);

        listener.handleDisconnect(event);

        verify(userService, times(1)).changeConnectStatus("testuser", ConnectionStatus.OFFLINE);
    }
}



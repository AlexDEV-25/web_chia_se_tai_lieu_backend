package com.example.app.listener;

import java.security.Principal;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.app.constant.ConnectionStatus;
import com.example.app.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
	private final UserService userService;

	@EventListener
	public void handleConnect(SessionConnectEvent event) {

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

		Principal principal = accessor.getUser();

		if (principal == null) {
			return;
		}

		userService.changeConnectStatus(principal.getName(), ConnectionStatus.ONLINE);

		System.out.println(principal.getName() + " connected");
	}

	@EventListener
	public void handleDisconnect(SessionDisconnectEvent event) {

		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

		Principal principal = accessor.getUser();

		if (principal == null) {
			return;
		}

		userService.changeConnectStatus(principal.getName(), ConnectionStatus.OFFLINE);

		System.out.println(principal.getName() + " disconnected");
	}
}

package com.example.app.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.app.helper.JwtService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

	private final JwtService jwtService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {

			String authHeader = accessor.getFirstNativeHeader("Authorization");

			if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {

				return message;
			}

			try {

				String token = authHeader.replace("Bearer ", "");

				Claims claims = jwtService.extractAllClaims(token);

				String username = claims.getSubject();

				String scope = claims.get("scope", String.class);

				List<SimpleGrantedAuthority> authorities = Arrays.stream(scope.split(" "))
						.map(SimpleGrantedAuthority::new).toList();

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
						authorities);

				accessor.setUser(auth);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		return message;
	}

}
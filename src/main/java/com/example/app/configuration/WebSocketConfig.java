package com.example.app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private final WebSocketAuthInterceptor interceptor;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		/*
		 * nơi frontend subscribe để nhận realtime nói dễ hiểu thì frontend nó đăng ký
		 * cái gì có tiền tố /topic như mình khai báo đều sẽ nhận được realtime
		 * stompClient.subscribe("/topic/messages", ...)
		 * stompClient.subscribe("/topic/abcxyz", ...)
		 */
		/*
		 * có 2 cách nhận: @SendTo(/topic/...) + return Obj trong controller hoặc tạo
		 * event trong service và lắng nghe nó
		 * messagingTemplate.convertAndSend("/topic/conversation/" +
		 * event.getMessage().getConversation().getId(), event.getMessage());
		 */
		registry.enableSimpleBroker("/topic");

		// nơi frontend gửi message lên server
		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		registry.addEndpoint("/ws") // đăng ký thêm endpoint với backend (http://localhost:8080/ws)
				.setAllowedOriginPatterns("*") // đăng ký đường dẫn frontend (http://localhost:5173)
				.withSockJS();// phòng tường hợp browser không hỗ trợ websocket chuẩn
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(interceptor);
	}
}

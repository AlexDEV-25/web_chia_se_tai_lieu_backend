package com.example.app.dto.response.chatmessage;

import java.time.LocalDateTime;

import com.example.app.constant.ConnectionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
	private Long id;
	private Long conversationId;
	private boolean me;
	private String message;
	private Long userId;
	private String userName;
	private String userAvatar;
	private ConnectionStatus userStatus;
	private LocalDateTime createdAt;
}

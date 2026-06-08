package com.example.app.dto.request;

import com.example.app.constant.ChatRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantInfoRequest {
	private Long userId;
	private Long conversationId;
	private ChatRole chatRole;
}

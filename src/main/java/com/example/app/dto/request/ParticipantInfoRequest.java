package com.example.app.dto.request;

import com.example.app.constant.ChatRole;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantInfoRequest {

	@NotNull(message = "userId không được để trống")
	private Long userId;

	@NotNull(message = "conversationId không được để trống")
	private Long conversationId;

	@NotNull(message = "chatRole không được null")
	private ChatRole chatRole;
}

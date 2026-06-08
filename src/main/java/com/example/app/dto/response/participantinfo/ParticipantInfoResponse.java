package com.example.app.dto.response.participantinfo;

import java.time.LocalDateTime;

import com.example.app.constant.ChatRole;
import com.example.app.constant.ConnectionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantInfoResponse {
	private Long id;
	private Long userId;
	private String userName;
	private LocalDateTime lastSeen;
	private ChatRole chatRole;
	private ConnectionStatus userStatus;
}

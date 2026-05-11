package com.example.app.dto.response.participantinfo;

import com.example.app.constant.ConnectionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantInfoResponse {
	private Long id;
	private Long userId;
	private String userName;
	private ConnectionStatus userStatus;
}

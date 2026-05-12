package com.example.app.dto.response.conversation;

import java.util.List;

import com.example.app.constant.ConversationType;
import com.example.app.dto.response.participantinfo.ParticipantInfoResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
	private Long id;
	private ConversationType type;
	private String conversationAvatar;// avatar của người mình đang nhắn tin
	private String conversationName;// tên của người mình đang nhắn tin
	private List<ParticipantInfoResponse> participantInfos;
}

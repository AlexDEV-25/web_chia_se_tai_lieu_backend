package com.example.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.constant.AppError;
import com.example.app.dto.request.ChatMessageRequest;
import com.example.app.dto.response.chatmessage.ChatMessageResponse;
import com.example.app.event.MessageCreatedEvent;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.ChatMessageMapper;
import com.example.app.model.ChatMessage;
import com.example.app.model.Conversation;
import com.example.app.model.User;
import com.example.app.repository.ChatMessageRepository;
import com.example.app.repository.ConversationRepository;
import com.example.app.repository.ParticipantInfoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;
	private final ConversationRepository conversationRepository;
	private final ParticipantInfoRepository participantInfoRepository;
	private final ApplicationEventPublisher publisher;
	private final ChatMessageMapper chatMessageMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('GET_MESSAGE')")
	public List<ChatMessageResponse> getMessages(Long conversationId) {
		User me = getUserByToken.get();

		if (me == null) {
			throw AppException.builder().appError(AppError.USER_NOT_FOUND).build();
		}

		List<ChatMessage> messages = chatMessageRepository.findAllByConversation_IdOrderByCreatedAtAsc(conversationId);
		return messages.stream().map((message) -> toChatMessageResponse(message, me)).toList();
	}

	@PreAuthorize("hasAuthority('CREATE_MESSAGE')")
	public ChatMessageResponse createMessage(ChatMessageRequest request) {
		User me = getUserByToken.get();

		if (me == null) {
			throw AppException.builder().appError(AppError.USER_NOT_FOUND).build();
		}

		Conversation conversation = conversationRepository.findById(request.getConversationId())
				.orElseThrow(() -> AppException.builder().appError(AppError.CONVERSATION_NOT_FOUND).build());

		boolean isParticipant = participantInfoRepository.existsByConversation_IdAndUser_Id(conversation.getId(),
				me.getId());

		if (!isParticipant) {
			throw AppException.builder().appError(AppError.NOT_CONVERSATION_MEMBER).build();
		}
		ChatMessage message = ChatMessage.builder().message(request.getMessage()).conversation(conversation).sender(me)
				.createdAt(LocalDateTime.now()).build();

		ChatMessage saved = chatMessageRepository.save(message);

		ChatMessageResponse response = toChatMessageResponse(saved, me);

		publisher.publishEvent(new MessageCreatedEvent(response));

		return response;

	}

	private ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage, User me) {
		ChatMessageResponse chatMessageResponse = chatMessageMapper.chatMessagetoChatMessageResponse(chatMessage);
		chatMessageResponse.setMe(me.getId().equals(chatMessage.getSender().getId()));
		return chatMessageResponse;
	}

}

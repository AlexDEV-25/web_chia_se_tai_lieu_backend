package com.example.app.service;

import java.time.LocalDateTime;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.constant.AppError;
import com.example.app.constant.ChatRole;
import com.example.app.constant.ConversationType;
import com.example.app.dto.request.ParticipantInfoRequest;
import com.example.app.dto.response.participantinfo.ParticipantInfoResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.ParticipantInfoMapper;
import com.example.app.model.Conversation;
import com.example.app.model.ParticipantInfo;
import com.example.app.model.User;
import com.example.app.repository.ParticipantInfoRepository;
import com.example.app.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ParticipantInfoService {
	private final ParticipantInfoRepository participantInfoRepository;
	private final UserRepository userRepository;
	private final ParticipantInfoMapper participantInfoMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('UPDATE_LAST_SEEN')")
	public ParticipantInfoResponse updateLastSeen(Long id) {
		User user = getUserByToken.get();
		ParticipantInfo participantInfo = participantInfoRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> AppException.builder().appError(AppError.PARTICIPANT_INFO_NOT_FOUND).build());
		participantInfo.setLastSeen(LocalDateTime.now());
		ParticipantInfo saved = participantInfoRepository.save(participantInfo);
		return participantInfoMapper.entityToResponse(saved);
	}

	@PreAuthorize("hasAuthority('ADD_MEMBER')")
	public ParticipantInfoResponse addMember(ParticipantInfoRequest request) {
		User user = getUserByToken.get();

		ParticipantInfo participantInfo = findByUserIdAndConversationId(user.getId(), request.getConversationId());
		if (participantInfo.getChatRole() == ChatRole.MEMBER) {
			throw AppException.builder().appError(AppError.ACCESS_DENIED).build();
		}
		Conversation conversation = participantInfo.getConversation();

		if (conversation.getType() == ConversationType.DIRECT) {
			throw AppException.builder().appError(AppError.CANNOT_ADD_MEMBER).build();
		}

		User newMember = userRepository.findById(request.getUserId())
				.orElseThrow(() -> AppException.builder().appError(AppError.USER_NOT_FOUND).build());

		ParticipantInfo newParticipantInfo = ParticipantInfo.builder().user(newMember).conversation(conversation)
				.chatRole(ChatRole.MEMBER).build();

		ParticipantInfo saved = participantInfoRepository.save(newParticipantInfo);
		return participantInfoMapper.entityToResponse(saved);
	}

	@PreAuthorize("hasAuthority('DELETE_MEMBER')")
	public void deleteMember(Long userId, Long conversationId) {
		User user = getUserByToken.get();

		ParticipantInfo myParticipantInfo = findByUserIdAndConversationId(user.getId(), conversationId);

		ParticipantInfo userParticipantInfo = findByUserIdAndConversationId(userId, conversationId);

		if (myParticipantInfo.getConversation().getId() != userParticipantInfo.getConversation().getId()) {
			throw AppException.builder().appError(AppError.NOT_CONVERSATION_MEMBER).build();
		}

		if (myParticipantInfo.getConversation().getType() == ConversationType.DIRECT) {
			throw AppException.builder().appError(AppError.CANNOT_REMOVE_MEMBER).build();
		}

		if (myParticipantInfo.getChatRole() == ChatRole.MEMBER
				|| (userParticipantInfo.getChatRole() == myParticipantInfo.getChatRole())
				|| (userParticipantInfo.getChatRole() == ChatRole.MANAGER)) {
			throw AppException.builder().appError(AppError.ACCESS_DENIED).build();
		}
		participantInfoRepository.delete(userParticipantInfo);
	}

	@PreAuthorize("hasAuthority('CHANGE_ROLE')")
	public ParticipantInfoResponse changeRole(@Valid ParticipantInfoRequest dto) {

		User user = getUserByToken.get();

		ParticipantInfo myParticipantInfo = findByUserIdAndConversationId(user.getId(), dto.getConversationId());

		ParticipantInfo userParticipantInfo = findByUserIdAndConversationId(dto.getUserId(), dto.getConversationId());

		if (myParticipantInfo.getChatRole() != ChatRole.MANAGER) {
			throw AppException.builder().appError(AppError.ACCESS_DENIED).build();
		}

		if (myParticipantInfo.getConversation().getId() != userParticipantInfo.getConversation().getId()) {
			throw AppException.builder().appError(AppError.NOT_CONVERSATION_MEMBER).build();
		}

		if (myParticipantInfo.getConversation().getType() == ConversationType.DIRECT) {
			throw AppException.builder().appError(AppError.CANNOT_CHANGE_ROLE).build();
		}

		userParticipantInfo.setChatRole(dto.getChatRole());

		ParticipantInfo saved = participantInfoRepository.save(userParticipantInfo);
		return participantInfoMapper.entityToResponse(saved);
	}

	private ParticipantInfo findByUserIdAndConversationId(Long userId, Long conversationId) {
		ParticipantInfo participantInfo = participantInfoRepository
				.findByUser_IdAndConversation_Id(userId, conversationId)
				.orElseThrow(() -> AppException.builder().appError(AppError.PARTICIPANT_INFO_NOT_FOUND).build());
		return participantInfo;
	}

}

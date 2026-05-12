package com.example.app.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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

	@PreAuthorize("hasRole('UPDATE_LAST_SEEN')")
	public ParticipantInfoResponse updateLastSeen(Long id) {
		User user = getUserByToken.get();
		ParticipantInfo participantInfo = participantInfoRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new AppException("không tìm thấy danh mục", 1001, HttpStatus.BAD_REQUEST));
		participantInfo.setLastSeen(LocalDateTime.now());
		ParticipantInfo saved = participantInfoRepository.save(participantInfo);
		return participantInfoMapper.entityToResponse(saved);
	}

	@PreAuthorize("hasRole('ADD_MEMBER')")
	public ParticipantInfoResponse addMember(ParticipantInfoRequest request) {
		User user = getUserByToken.get();

		ParticipantInfo participantInfo = findByUserIdAndConversationId(user.getId(), request.getConversationId());
		if (participantInfo.getChatRole() == ChatRole.MEMBER) {
			throw new AppException("Không đủ quyền hạn", 1001, HttpStatus.BAD_REQUEST);
		}
		Conversation conversation = participantInfo.getConversation();

		if (conversation.getType() == ConversationType.DIRECT) {
			throw new AppException("không thể thêm thành viên", 1001, HttpStatus.BAD_REQUEST);
		}

		User newMember = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new AppException("không tìm thấy người dùng", 1001, HttpStatus.BAD_REQUEST));

		ParticipantInfo newParticipantInfo = ParticipantInfo.builder().user(newMember).conversation(conversation)
				.chatRole(ChatRole.MEMBER).build();

		ParticipantInfo saved = participantInfoRepository.save(newParticipantInfo);
		return participantInfoMapper.entityToResponse(saved);
	}

	@PreAuthorize("hasRole('DELETE_MEMBER')")
	public void deleteMember(Long userId, Long conversationId) {
		User user = getUserByToken.get();

		ParticipantInfo myParticipantInfo = findByUserIdAndConversationId(user.getId(), conversationId);

		ParticipantInfo userParticipantInfo = findByUserIdAndConversationId(userId, conversationId);

		if (myParticipantInfo.getConversation().getId() != userParticipantInfo.getConversation().getId()) {
			throw new AppException("không cùng nhóm chat", 1001, HttpStatus.BAD_REQUEST);
		}

		if (myParticipantInfo.getConversation().getType() == ConversationType.DIRECT) {
			throw new AppException("không thể xóa thành viên", 1001, HttpStatus.BAD_REQUEST);
		}

		if (myParticipantInfo.getChatRole() == ChatRole.MEMBER
				|| (userParticipantInfo.getChatRole() == myParticipantInfo.getChatRole())
				|| (userParticipantInfo.getChatRole() == ChatRole.MANAGER)) {
			throw new AppException("Không đủ quyền hạn", 1001, HttpStatus.BAD_REQUEST);
		}
		participantInfoRepository.delete(userParticipantInfo);
	}

	@PreAuthorize("hasRole('CHANGE_ROLE')")
	public ParticipantInfoResponse changeRole(@Valid ParticipantInfoRequest dto) {

		User user = getUserByToken.get();

		ParticipantInfo myParticipantInfo = findByUserIdAndConversationId(user.getId(), dto.getConversationId());

		ParticipantInfo userParticipantInfo = findByUserIdAndConversationId(dto.getUserId(), dto.getConversationId());

		if (myParticipantInfo.getChatRole() != ChatRole.MANAGER) {
			throw new AppException("không đủ quyền hạn", 1001, HttpStatus.BAD_REQUEST);
		}

		if (myParticipantInfo.getConversation().getId() != userParticipantInfo.getConversation().getId()) {
			throw new AppException("không cùng nhóm chat", 1001, HttpStatus.BAD_REQUEST);
		}

		if (myParticipantInfo.getConversation().getType() == ConversationType.DIRECT) {
			throw new AppException("không thể đổi vai trò", 1001, HttpStatus.BAD_REQUEST);
		}

		userParticipantInfo.setChatRole(dto.getChatRole());

		ParticipantInfo saved = participantInfoRepository.save(userParticipantInfo);
		return participantInfoMapper.entityToResponse(saved);
	}

	private ParticipantInfo findByUserIdAndConversationId(Long userId, Long conversationId) {
		ParticipantInfo participantInfo = participantInfoRepository
				.findByUser_IdAndConversation_Id(userId, conversationId)
				.orElseThrow(() -> new AppException("không tìm thấy thông tin", 1001, HttpStatus.BAD_REQUEST));
		return participantInfo;
	}

}

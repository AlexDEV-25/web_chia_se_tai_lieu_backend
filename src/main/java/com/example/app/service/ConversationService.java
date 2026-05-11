package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.app.constant.ConversationType;
import com.example.app.dto.request.ConversationRequest;
import com.example.app.dto.response.conversation.ConversationResponse;
import com.example.app.dto.response.participantinfo.ParticipantInfoResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.ConversationMapper;
import com.example.app.mapper.ParticipantInfoMapper;
import com.example.app.model.Conversation;
import com.example.app.model.ParticipantInfo;
import com.example.app.model.User;
import com.example.app.repository.ConversationRepository;
import com.example.app.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConversationService {
	private final ConversationRepository conversationRepository;
	private final UserRepository userRepository;
	private final ConversationMapper conversationMapper;
	private final ParticipantInfoMapper participantInfoMapper;
	private final GetUserByToken getUserByToken;

	@PreAuthorize("hasAuthority('GET_MY_CONVERSATION')")
	public List<ConversationResponse> getMyConversations() {
		User me = getUserByToken.get();
		List<Conversation> conversations = conversationRepository.findMyConversations(me.getId());
		List<ConversationResponse> conversationResponses = conversations.stream()
				.map(conversation -> toConversationResponse(conversation, me)).toList();
		return conversationResponses;
	}

	@Transactional
	@PreAuthorize("hasAuthority('CREATE_DIRECT_CONVERSATION')")
	public ConversationResponse createDirectConversation(ConversationRequest request) {
		User me = getUserByToken.get();

		if (request.getParticipantIds().size() != 1) {
			throw new AppException("Chat 1-1 chỉ được có 1 người", 1001, HttpStatus.BAD_REQUEST);
		}

		Long partnerId = request.getParticipantIds().getFirst();
		User partner = userRepository.findByIdAndHideFalse(partnerId)
				.orElseThrow(() -> new AppException("user không tồn tại", 1001, HttpStatus.BAD_REQUEST));

		List<Long> participantIds = new ArrayList<>(request.getParticipantIds());
		participantIds.add(me.getId());

		Optional<Conversation> existing = conversationRepository.findExactConversation(participantIds, 2);
		if (existing.isPresent()) {
			return toConversationResponse(existing.get(), me);
		}

		Conversation entity = Conversation.builder().type(request.getType()).createdAt(LocalDateTime.now()).build();
		List<ParticipantInfo> participants = buildListParticipantInfo(me, partner, entity);
		entity.setParticipantInfos(participants);
		Conversation conversation = conversationRepository.save(entity);
		return toConversationResponse(conversation, me);
	}

	@Transactional
	@PreAuthorize("hasAuthority('CREATE_GROUP_CONVERSATION')")
	public ConversationResponse createGroupConversation(ConversationRequest request) {

		User me = getUserByToken.get();

		// 1. validate
		if (request.getParticipantIds() == null || request.getParticipantIds().size() < 2) {
			throw new AppException("Group phải có ít nhất 2 người khác", 1001, HttpStatus.BAD_REQUEST);
		}

		// tránh trùng + tránh thêm chính mình nhiều lần
		Set<Long> uniqueIds = new HashSet<>(request.getParticipantIds());

		if (uniqueIds.contains(me.getId())) {
			throw new AppException("Không cần truyền chính bạn vào group", 1002, HttpStatus.BAD_REQUEST);
		}

		// 2. lấy user
		List<User> users = userRepository.findAllById(uniqueIds);

		if (users.size() != uniqueIds.size()) {
			throw new AppException("Có user không tồn tại", 1003, HttpStatus.BAD_REQUEST);
		}

		// 3. tạo conversation
		Conversation conversation = Conversation.builder().type(request.getType()).createdAt(LocalDateTime.now())
				.build();

		StringBuilder tenNhom = new StringBuilder();

		List<ParticipantInfo> participants = new ArrayList<>();

		// thêm mình
		ParticipantInfo meParticipant = new ParticipantInfo();
		meParticipant.setUser(me);
		tenNhom.append(me.getUsername() + ", ");

		meParticipant.setConversation(conversation);
		participants.add(meParticipant);

		// thêm các user khác
		for (User user : users) {
			ParticipantInfo p = new ParticipantInfo();
			p.setUser(user);
			p.setConversation(conversation);
			tenNhom.append(user.getUsername() + ", ");
			participants.add(p);
		}

		conversation.setGroupName("Nhóm " + tenNhom);
		conversation.setParticipantInfos(participants);

		// 4. save (cascade ALL → save luôn participant)
		conversationRepository.save(conversation);

		return toConversationResponse(conversation, me);
	}

	private List<ParticipantInfo> buildListParticipantInfo(User me, User partner, Conversation conversation) {
		List<ParticipantInfo> participants = new ArrayList<>();

		ParticipantInfo p1 = new ParticipantInfo();
		p1.setUser(me);
		p1.setConversation(conversation);

		ParticipantInfo p2 = new ParticipantInfo();
		p2.setUser(partner);
		p2.setConversation(conversation);

		participants.add(p1);
		participants.add(p2);

		return participants;
	}

	private ConversationResponse toConversationResponse(Conversation conversation, User me) {
		ConversationResponse conversationResponse = conversationMapper.toConversationResponse(conversation);
		if (conversation.getType() == ConversationType.DIRECT) {
			ParticipantInfo partnerInfo = conversation.getParticipantInfos().stream()
					.filter(p -> !p.getUser().getId().equals(me.getId())).findFirst().orElseThrow();

			User partner = partnerInfo.getUser();
			conversationResponse.setConversationAvatar(partner.getAvatarUrl());
			conversationResponse.setConversationName(partner.getUsername());
		} else {
			conversationResponse.setConversationAvatar(conversation.getGroupAvatarUrl());
			conversationResponse.setConversationName(conversation.getGroupName());
		}
		List<ParticipantInfoResponse> participantInfoResponse = conversation.getParticipantInfos().stream()
				.map(participantInfoMapper::entityToResponse).toList();
		conversationResponse.setParticipantInfos(participantInfoResponse);
		return conversationResponse;
	}

}

package com.example.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.constant.AppError;
import com.example.app.constant.ChatRole;
import com.example.app.constant.ConversationType;
import com.example.app.dto.request.ConversationGroupRequest;
import com.example.app.dto.request.ConversationRequest;
import com.example.app.dto.response.conversation.ConversationResponse;
import com.example.app.dto.response.participantinfo.ParticipantInfoResponse;
import com.example.app.exception.AppException;
import com.example.app.helper.FileManager;
import com.example.app.helper.GetUserByToken;
import com.example.app.mapper.ConversationMapper;
import com.example.app.mapper.ParticipantInfoMapper;
import com.example.app.model.Conversation;
import com.example.app.model.ParticipantInfo;
import com.example.app.model.User;
import com.example.app.repository.ConversationRepository;
import com.example.app.repository.ParticipantInfoRepository;
import com.example.app.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ConversationService {
	private final ConversationRepository conversationRepository;
	private final UserRepository userRepository;
	private final ParticipantInfoRepository participantInfoRepository;
	private final ConversationMapper conversationMapper;
	private final ParticipantInfoMapper participantInfoMapper;
	private final FileManager fileStorage;
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
			throw AppException.builder().appError(AppError.MEMBER_LIMIT_EXCEEDED).build();
		}

		Long partnerId = request.getParticipantIds().getFirst();
		User partner = userRepository.findByIdAndHideFalse(partnerId)
				.orElseThrow(() -> AppException.builder().appError(AppError.USER_NOT_FOUND).build());

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
	public ConversationResponse createGroupConversation(MultipartFile avt, ConversationGroupRequest request) {

		User me = getUserByToken.get();

		// 1. validate
		if (request.getParticipantIds() == null || request.getParticipantIds().size() < 2) {
			throw AppException.builder().appError(AppError.MEMBER_COUNT_TOO_SMALL).build();
		}

		// tránh trùng + tránh thêm chính mình nhiều lần
		Set<Long> uniqueIds = new HashSet<>(request.getParticipantIds());

		// 2. lấy user
		List<User> users = userRepository.findAllById(uniqueIds);

		if (users.size() != uniqueIds.size()) {
			throw AppException.builder().appError(AppError.INVALID_MEMBER_COUNT).build();
		}

		String groupAvt = avt != null ? saveGroupAvt(avt) : null;
		String groupName = request.getGroupName() != null ? request.getGroupName()
				: "Nhóm " + (request.getParticipantIds().size() + 1) + " người";

		// 3. tạo conversation
		Conversation conversation = Conversation.builder().type(request.getType()).groupAvatarUrl(groupAvt)
				.groupName(groupName).createdAt(LocalDateTime.now()).build();

		List<ParticipantInfo> participants = new ArrayList<>();

		// thêm mình
		ParticipantInfo meParticipant = new ParticipantInfo();
		meParticipant.setUser(me);
		meParticipant.setChatRole(ChatRole.MANAGER);
		meParticipant.setConversation(conversation);
		participants.add(meParticipant);

		// thêm các user khác
		for (User user : users) {
			ParticipantInfo p = new ParticipantInfo();
			p.setUser(user);
			meParticipant.setChatRole(ChatRole.MEMBER);
			p.setConversation(conversation);
			participants.add(p);
		}

		conversation.setParticipantInfos(participants);

		// 4. save (cascade ALL → save luôn participant)
		conversationRepository.save(conversation);

		return toConversationResponse(conversation, me);
	}

	@PreAuthorize("hasAuthority('SEARCH_CONVERSATION')")
	public List<ConversationResponse> search(String keyword) {
		User me = getUserByToken.get();
		List<Conversation> conversations = conversationRepository.search(me.getId(), keyword);
		return conversations.stream().map(conversation -> toConversationResponse(conversation, me)).toList();
	}

	@PreAuthorize("hasAuthority('GET_DETAIL_CONVERSATION')")
	public ConversationResponse getDetailConversation(Long id) {
		User me = getUserByToken.get();

		Conversation conversation = conversationRepository.findById(id)
				.orElseThrow(() -> AppException.builder().appError(AppError.CONVERSATION_NOT_FOUND).build());

		if (!participantInfoRepository.existsByConversation_IdAndUser_Id(id, me.getId())) {
			throw AppException.builder().appError(AppError.NOT_CONVERSATION_MEMBER).build();
		}

		return toConversationResponse(conversation, me);
	}

	private String saveGroupAvt(MultipartFile avt) {
		try {
			Map<?, ?> handleAvt = fileStorage.uploadImage(avt);
			String avatarUrl = (String) handleAvt.get("secure_url");
			return avatarUrl;
		} catch (Exception e) {
			throw AppException.builder().appError(AppError.UPDATE_AVATAR_FAILED).build();
		}
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

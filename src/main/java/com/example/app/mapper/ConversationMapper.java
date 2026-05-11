package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.response.conversation.ConversationResponse;
import com.example.app.model.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
	@Mapping(target = "conversationAvatar", ignore = true)
	@Mapping(target = "conversationName", ignore = true)
	@Mapping(target = "participantInfos", ignore = true)
	ConversationResponse toConversationResponse(Conversation conversation);
}

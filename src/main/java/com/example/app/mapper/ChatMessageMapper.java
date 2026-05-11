package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.response.chatmessage.ChatMessageResponse;
import com.example.app.model.ChatMessage;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

	@Mapping(target = "me", ignore = true)
	@Mapping(target = "conversationId", source = "conversation.id")
	@Mapping(target = "userId", source = "sender.id")
	@Mapping(target = "userName", source = "sender.username")
	@Mapping(target = "userAvatar", source = "sender.avatarUrl")
	@Mapping(target = "userStatus", source = "sender.status")
	ChatMessageResponse chatMessagetoChatMessageResponse(ChatMessage entity);
}

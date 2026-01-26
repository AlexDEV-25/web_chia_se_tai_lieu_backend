package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.request.UserNotificationRequest;
import com.example.app.dto.response.UserNotificationResponse;
import com.example.app.model.UserNotification;

@Mapper(componentModel = "spring")
public interface UserNotificationMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "sender", ignore = true)
	@Mapping(target = "receiver", ignore = true)
	@Mapping(target = "notification", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	UserNotification requestToUserNotification(UserNotificationRequest request);

	@Mapping(source = "sender.id", target = "senderId")
	@Mapping(source = "sender.username", target = "senderName")
	@Mapping(source = "receiver.id", target = "receiverId")
	@Mapping(source = "receiver.username", target = "receiverName")
	@Mapping(source = "notification.id", target = "notificationId")
	@Mapping(source = "notification.content", target = "notificationContent")
	@Mapping(source = "notification.type", target = "notificationType")
	UserNotificationResponse userNotificationToResponse(UserNotification entity);
}

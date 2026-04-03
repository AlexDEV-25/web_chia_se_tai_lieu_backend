package com.example.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.app.dto.request.NotificationRequest;
import com.example.app.dto.response.notification.NotificationResponse;
import com.example.app.model.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
	@Mapping(target = "id", ignore = true)
	Notification requestToNotification(NotificationRequest request);

	NotificationResponse notificationToResponse(Notification entity);
}

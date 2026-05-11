package com.example.app.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.app.constant.ContentStatus;
import com.example.app.constant.NotificationAction;
import com.example.app.constant.NotificationType;
import com.example.app.dto.response.document.DocumentEventDTO;
import com.example.app.event.DocumentStatusEvent;
import com.example.app.helper.NotificationHelper;
import com.example.app.model.User;
import com.example.app.service.NotificationService;
import com.example.app.service.UserNotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentStatusListener implements NotificationHelper {
	private final NotificationService notificationService;
	private final UserNotificationService userNotificationService;

	@Value("${app.domain.frontend}")
	private String frontendDomain;

	@Override
	public NotificationService notificationService() {
		return notificationService;
	}

	@Override
	public UserNotificationService userNotificationService() {
		return userNotificationService;
	}

	@EventListener
	public void handle(DocumentStatusEvent event) {
		DocumentEventDTO document = event.getDocument();
		User sender = event.getSender();
		User receiver = document.getUser();
		// admin gửi thông báo cho tác giả và người theo dõi của họ khi tài liệu được
		// phê duyệt
		if (event.getAction() == NotificationAction.PUBLIC) {
			String link = frontendDomain + "/document/" + document.getId();
			String contentToAuthor = "Tài Liệu \" " + document.getTitle() + "\" đã được duyệt";
			sendToAuthor(contentToAuthor, link, sender, receiver, NotificationType.INFO);
			String contentToAudiences = "người dùng \" " + receiver.getUsername() + "\" đã đăng tài liệu mới";
			sendToFollower(contentToAudiences, link, sender, receiver, NotificationType.INFO);
		}
		// admin gửi thông báo cho tác giả và người theo dõi của họ khi tài liệu tạm ẩn
		else if (event.getAction() == NotificationAction.ADMIN_HIDDEN) {
			String contentToAuthor = "Tài Liệu \" " + document.getTitle() + "\" của bạn tạm thời bị ẩn";
			sendToAuthor(contentToAuthor, null, sender, receiver, NotificationType.WARNING);

			String contentToAudiences = "Tài liệu: \" " + document.getTitle() + "\" của tác giả"
					+ receiver.getUsername() + " tạm thời bị ẩn";
			sendToFollower(contentToAudiences, null, sender, receiver, NotificationType.INFO);
		}
		// admin gửi thông báo cho tác giả khi tài liệu bị xóa và người theo dõi của họ
		// nếu tài liệu đã được duyệt trước đó
		else if (event.getAction() == NotificationAction.ADMIN_DELETE) {
			String contentToAuthor = "Tài Liệu \" " + document.getTitle() + "\" của bạn đã bị xóa";
			sendToAuthor(contentToAuthor, null, sender, receiver, NotificationType.WARNING);
			if (event.getDocument().getStatus() != ContentStatus.PENDING) {
				String contentToAudiences = "Tài liệu: \" " + document.getTitle() + "\" của tác giả"
						+ receiver.getUsername() + "  đã bị xóa";
				sendToFollower(contentToAudiences, null, sender, receiver, NotificationType.INFO);
			}
		}
		// tác giả gửi thông báo tạo ẩn tài liệu
		else if (event.getAction() == NotificationAction.AUTHOR_HIDDEN) {
			String contentToAudiences = "Tác giả " + receiver.getUsername() + " đã ẩn" + "Tài liệu: \""
					+ document.getTitle() + "\"";
			sendToFollower(contentToAudiences, null, sender, receiver, NotificationType.INFO);
		}
		// tác giả gửi thông báo xóa ẩn tài liệu
		else if (event.getAction() == NotificationAction.AUTHOR_DELETE) {
			String contentToAudiences = "Tác giả " + receiver.getUsername() + " đã xóa" + "Tài liệu: \""
					+ document.getTitle() + "\"";
			sendToFollower(contentToAudiences, null, sender, receiver, NotificationType.INFO);
		}

	}

}

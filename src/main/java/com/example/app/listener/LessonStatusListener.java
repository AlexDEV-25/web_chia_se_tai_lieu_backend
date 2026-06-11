package com.example.app.listener;

import com.example.app.constant.ContentStatus;
import com.example.app.constant.NotificationAction;
import com.example.app.constant.NotificationType;
import com.example.app.dto.response.lesson.LessonEventDTO;
import com.example.app.event.LessonStatusEvent;
import com.example.app.helper.NotificationHelper;
import com.example.app.model.User;
import com.example.app.service.NotificationService;
import com.example.app.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonStatusListener implements NotificationHelper {
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
    public void handle(LessonStatusEvent event) {
        LessonEventDTO lesson = event.getLesson();
        User sender = event.getSender();
        User receiver = lesson.getUser();
        // admin gửi thông báo cho tác giả và người theo dõi của họ khi Bài giảng được
        // phê duyệt
        if (event.getAction() == NotificationAction.PUBLIC) {
            String link = frontendDomain + "/lesson/" + lesson.getId();
            String contentToAuthor = "Bài giảng \" " + lesson.getTitle() + "\" đã được duyệt";
            sendToAuthor(contentToAuthor, link, sender, receiver, NotificationType.INFO);
            String contentToAudiences = "người dùng \" " + receiver.getUsername() + "\" đã đăng bài giảng mới";
            sendToFollower(contentToAudiences, link, sender, receiver, NotificationType.INFO);
        }
        // admin gửi thông báo cho tác giả và người theo dõi của họ khi Bài giảng tạm ẩn
        else if (event.getAction() == NotificationAction.ADMIN_HIDDEN) {
            String contentToAuthor = "Bài giảng \" " + lesson.getTitle() + "\" của bạn tạm thời bị ẩn";
            sendToAuthor(contentToAuthor, null, sender, receiver, NotificationType.WARNING);

            String contentToAudiences = "Bài giảng: \" " + lesson.getTitle() + "\" của tác giả" + receiver.getUsername()
                    + " tạm thời bị ẩn";
            sendToFollower(contentToAudiences, null, sender, receiver, NotificationType.INFO);
        }
        // admin gửi thông báo cho tác giả khi Bài giảng bị xóa và người theo dõi của họ
        // nếu Bài giảng đã được duyệt trước đó
        else if (event.getAction() == NotificationAction.ADMIN_DELETE) {
            String contentToAuthor = "Bài giảng \" " + lesson.getTitle() + "\" của bạn đã bị xóa";
            sendToAuthor(contentToAuthor, null, sender, receiver, NotificationType.WARNING);
            if (event.getLesson().getStatus() != ContentStatus.PENDING) {
                String contentToAudiences = "Bài giảng: \" " + lesson.getTitle() + "\" của tác giả"
                        + receiver.getUsername() + "  đã bị xóa";
                sendToFollower(contentToAudiences, null, sender, receiver, NotificationType.INFO);
            }
        }
        // tác giả gửi thông báo tạo ẩn Bài giảng
        else if (event.getAction() == NotificationAction.AUTHOR_HIDDEN) {
            String contentToAudiences = "Tác giả " + receiver.getUsername() + " đã ẩn" + "bài giảng: \""
                    + lesson.getTitle() + "\"";
            sendToFollower(contentToAudiences, null, sender, receiver, NotificationType.INFO);
        }
        // tác giả gửi thông báo xóa ẩn Bài giảng
        else if (event.getAction() == NotificationAction.AUTHOR_DELETE) {
            String contentToAudiences = "Tác giả " + receiver.getUsername() + " đã xóa" + "bài giảng: \""
                    + lesson.getTitle() + "\"";
            sendToFollower(contentToAudiences, null, sender, receiver, NotificationType.INFO);
        }

    }

}

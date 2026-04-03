package com.example.app.share;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.app.dto.request.NotificationRequest;
import com.example.app.dto.request.UserNotificationRequest;
import com.example.app.exception.AppException;
import com.example.app.mapper.NotificationMapper;
import com.example.app.mapper.UserNotificationMapper;
import com.example.app.model.Comment;
import com.example.app.model.Notification;
import com.example.app.model.User;
import com.example.app.model.UserFollow;
import com.example.app.model.UserNotification;
import com.example.app.repository.CommentRepository;
import com.example.app.repository.NotificationRepository;
import com.example.app.repository.UserFollowRepository;
import com.example.app.repository.UserNotificationRepository;
import com.example.app.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SendNotification {
	private final UserRepository userRepository;
	private final UserNotificationRepository userNotificationRepository;
	private final UserNotificationMapper userNotificationMapper;
	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;
	private final UserFollowRepository userFollowRepository;
	private final CommentRepository commentRepository;

	@Transactional
	private Long saveNotification(String content, String link, NotificationType type) {
		NotificationRequest request = new NotificationRequest(content, link, type);
		Notification notification = notificationMapper.requestToNotification(request);
		Notification saved = notificationRepository.save(notification);
		return saved.getId();
	}

	@Transactional
	private boolean saveUserNotification(Long senderId, Long receiverId, Long notificationId) {
		UserNotificationRequest request = new UserNotificationRequest(senderId, receiverId, notificationId, false);
		UserNotification userNotification = userNotificationMapper.requestToUserNotification(request);

		User sender = senderId != null ? userRepository.findById(senderId)
				.orElseThrow(() -> new AppException("sender không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		userNotification.setSender(sender);

		Notification notification = notificationId != null ? notificationRepository.findById(notificationId)
				.orElseThrow(() -> new AppException("notification không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		userNotification.setNotification(notification);

		User receiver = receiverId != null ? userRepository.findById(receiverId)
				.orElseThrow(() -> new AppException("receiver không tồn tại", 1001, HttpStatus.BAD_REQUEST)) : null;
		userNotification.setReceiver(receiver);

		userNotification.setCreatedAt(LocalDateTime.now());
		UserNotification saved = userNotificationRepository.save(userNotification);
		return (saved != null) ? true : false;
	}

	public void sendNotificationPublished(Status dtoStatus, Status entityStatus, Long entityId, String entityTitle,
			String userName, Long userId, User admin, Type type) {
		if (dtoStatus != entityStatus && dtoStatus == Status.PUBLISHED) {
			String head = "";
			String url = "";
			head = (type == Type.DOCUMENT) ? "Tài liệu" : "Bài giảng";
			url = (type == Type.DOCUMENT) ? "http://localhost:5173/document/" + entityId
					: "http://localhost:5173/lesson/" + entityId;

			Long NotificationId = saveNotification(head + " \" " + entityTitle + "\" của bạn đã được duyệt", url,
					NotificationType.INFO);
			if (saveUserNotification(admin.getId(), userId, NotificationId) == false) {
				throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
			}

			Long NotificationFollowId = saveNotification("người dùng \" " + userName + "\" đã đăng " + head + " mới",
					url, NotificationType.INFO);
			if (saveUserNotification(admin.getId(), userId, NotificationFollowId) == false) {
				throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
			}

			sendToFollower(userId, NotificationFollowId);
		}
	}

	public void sendNotificationHide(boolean dtoIsHide, boolean entityIsHide, String entityTitle, Long userId,
			User admin, Type type) {
		if (dtoIsHide != entityIsHide && dtoIsHide == true) {
			String head = "";
			head = (type == Type.DOCUMENT) ? "Tài liệu" : "Bài giảng";

			Long NotificationId = saveNotification(head + " \" " + entityTitle + "\" của bạn tạm thời bị ẩn", null,
					NotificationType.WARNING);
			if (saveUserNotification(admin.getId(), userId, NotificationId) == false) {
				throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
			}

			Long NotificationFollowId = saveNotification("Tài liệu \" " + entityTitle + "\" tạm thời đã bị ẩn", null,
					NotificationType.INFO);
			if (saveUserNotification(admin.getId(), userId, NotificationFollowId) == false) {
				throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
			}

			sendToFollower(userId, NotificationFollowId);
		}
	}

	public void sendNotificationDelete(String entityTitle, Long userId, User admin, Type type) {
		String head = "";
		head = (type == Type.DOCUMENT) ? "Tài liệu" : "Bài giảng";
		Long NotificationId = saveNotification(head + " \" " + entityTitle + "\"của bạn đã bị xóa", null,
				NotificationType.ERROR);
		if (saveUserNotification(admin.getId(), userId, NotificationId) == false) {
			throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
		}
		Long NotificationFollowId = saveNotification(head + " \" " + entityTitle + "\" đã bị xóa", null,
				NotificationType.INFO);
		if (saveUserNotification(admin.getId(), userId, NotificationFollowId) == false) {
			throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
		}

		sendToFollower(userId, NotificationFollowId);
	}

	public void sendNotificationMyHide(boolean dtoIsHide, boolean entityIsHide, String entityTitle, Long userId,
			String userName, Type type) {
		if (dtoIsHide != entityIsHide && dtoIsHide == true) {
			String head = "";
			head = (type == Type.DOCUMENT) ? "Tài liệu" : "Bài giảng";
			Long NotificationFollowId = saveNotification(userName + " đã tạm ẩn " + head + " \" " + entityTitle + " \"",
					null, NotificationType.INFO);
			sendToFollower(userId, NotificationFollowId);
		}
	}

	public void sendNotificationMyDelete(String entityTitle, Long userId, String userName, Type type) {
		String head = "";
		head = (type == Type.DOCUMENT) ? "Tài liệu" : "Bài giảng";

		Long NotificationFollowId = saveNotification(
				userName + " đã tạm thời ẩn " + head + " \" " + entityTitle + " \"", null, NotificationType.INFO);
		sendToFollower(userId, NotificationFollowId);
	}

	public void sendNotificationCommentReply(Long parentId, User user) {
		if (parentId != 0) {
			Comment cmt = commentRepository.findById(parentId)
					.orElseThrow(() -> new AppException("comment không tồn tại", 1001, HttpStatus.BAD_REQUEST));
			String url = "";
			url = (cmt.getType() == Type.DOCUMENT) ? "http://localhost:5173/document/" + cmt.getDocument().getId()
					: "http://localhost:5173/lesson/" + cmt.getLesson().getId();

			User receiver = cmt.getUser();
			if (receiver != null && user != null && !receiver.getId().equals(user.getId())) {
				Long NotificationId = saveNotification(
						"người dùng \" " + user.getUsername() + "\" đã trở lời bình luận của bạn", url,
						NotificationType.INFO);

				if (saveUserNotification(user.getId(), receiver.getId(), NotificationId) == false) {
					throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
				}
			}

		}
	}

	public void sendNotificationFollow(User follower, User following) {

		String url = "http://localhost:5173/profile/" + follower.getId();

		Long NotificationId = saveNotification("người dùng \" " + follower.getUsername() + "\" đã theo dõi bạn", url,
				NotificationType.INFO);

		if (saveUserNotification(follower.getId(), following.getId(), NotificationId) == false) {
			throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
		}
	}

	private void sendToFollower(Long userId, Long NotificationId) {
		List<UserFollow> ListFollower = userFollowRepository.findByFollowing_Id(userId);
		for (UserFollow uf : ListFollower) {
			if (saveUserNotification(userId, uf.getFollower().getId(), NotificationId) == false) {
				throw new AppException("Gửi thông báo không thành công", 1001, HttpStatus.BAD_REQUEST);
			}
		}
	}
}

package com.example.app.constant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppError {

	MISSING_TOKEN(1001, "Thiếu token xác thực", HttpStatus.UNAUTHORIZED),

	UPDATE_AVATAR_FAILED(1002, "Cập nhật ảnh đại diện nhóm thất bại", HttpStatus.BAD_REQUEST),

	UPLOAD_DOCUMENT_FAILED(1003, "Upload tài liệu thất bại", HttpStatus.BAD_REQUEST),

	UPLOAD_LECTURE_FAILED(1004, "Upload bài giảng thất bại", HttpStatus.BAD_REQUEST),

	UPDATE_PROFILE_FAILED(1005, "Cập nhật thông tin cá nhân thất bại", HttpStatus.BAD_REQUEST),

	INVALID_IMAGE_FORMAT(1006, "Ảnh không đúng định dạng", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

	INVALID_VIDEO_FORMAT(1007, "Video không đúng định dạng", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

	INVALID_SUBFILE_FORMAT(1008, "Subfile không đúng định dạng", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

	INVALID_DOCUMENT_FORMAT(1009, "Tài liệu không đúng định dạng", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

	TOKEN_EXPIRED(1010, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),

	INVALID_TOKEN(1011, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),

	INVALID_SECRET_KEY_LENGTH(1012, "Độ dài secret key không hợp lệ", HttpStatus.INTERNAL_SERVER_ERROR),

	JOSE_PROCESSING_ERROR(1013, "Lỗi xử lý JWT/JOSE", HttpStatus.INTERNAL_SERVER_ERROR),

	CREATE_NOTIFICATION_FAILED(1014, "Tạo thông báo thất bại", HttpStatus.BAD_REQUEST),

	USER_NOT_FOUND(1015, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),

	ACCOUNT_NOT_ACTIVATED(1016, "Tài khoản chưa được kích hoạt", HttpStatus.FORBIDDEN),

	EMAIL_ALREADY_EXISTS(1017, "Email đã tồn tại", HttpStatus.CONFLICT),

	USERNAME_ALREADY_EXISTS(1018, "Username đã tồn tại", HttpStatus.CONFLICT),

	ACCOUNT_ALREADY_ACTIVATED(1019, "Tài khoản đã được kích hoạt", HttpStatus.BAD_REQUEST),

	INVALID_VERIFICATION_CODE(1020, "Mã xác thực không đúng", HttpStatus.BAD_REQUEST),

	EMAIL_NOT_FOUND(1021, "Email không tồn tại", HttpStatus.NOT_FOUND),

	GOOGLE_LOGIN_FAILED(1022, "Đăng nhập bằng Google thất bại", HttpStatus.UNAUTHORIZED),

	CATEGORY_NOT_FOUND(1023, "Không tìm thấy danh mục", HttpStatus.NOT_FOUND),

	TYPE_NOT_FOUND(1024, "Không tìm thấy type", HttpStatus.NOT_FOUND),

	CONVERSATION_NOT_FOUND(1025, "Không tìm thấy cuộc hội thoại", HttpStatus.NOT_FOUND),

	NOT_CONVERSATION_MEMBER(1026, "Bạn không thuộc cuộc hội thoại này", HttpStatus.FORBIDDEN),

	LECTURE_NOT_FOUND(1027, "Không tìm thấy bài giảng", HttpStatus.NOT_FOUND),

	DOCUMENT_NOT_FOUND(1028, "Không tìm thấy tài liệu", HttpStatus.NOT_FOUND),

	MEMBER_LIMIT_EXCEEDED(1029, "Số lượng thành viên vượt quá giới hạn", HttpStatus.BAD_REQUEST),

	MEMBER_COUNT_TOO_SMALL(1030, "Số lượng thành viên quá ít", HttpStatus.BAD_REQUEST),

	INVALID_MEMBER_COUNT(1031, "Số lượng thành viên không hợp lệ", HttpStatus.BAD_REQUEST),

	ADD_TO_FAVORITE_FAILED(1032, "Thêm vào kho yêu thích thất bại", HttpStatus.BAD_REQUEST),

	REMOVE_FROM_FAVORITE_FAILED(1033, "Xóa khỏi kho yêu thích thất bại", HttpStatus.BAD_REQUEST),

	SUBFILE_NOT_FOUND(1034, "Không tìm thấy subfile", HttpStatus.NOT_FOUND),

	PARTICIPANT_INFO_NOT_FOUND(1035, "Không tìm thấy thông tin người tham gia", HttpStatus.NOT_FOUND),

	ACCESS_DENIED(1036, "Bạn không có quyền thực hiện chức năng này", HttpStatus.FORBIDDEN),

	CANNOT_ADD_MEMBER(1037, "Không thể thêm thành viên", HttpStatus.BAD_REQUEST),

	CANNOT_REMOVE_MEMBER(1038, "Không thể xóa thành viên", HttpStatus.BAD_REQUEST),

	CANNOT_CHANGE_ROLE(1039, "Không thể thay đổi vai trò", HttpStatus.BAD_REQUEST),

	ALREADY_RATED(1040, "Bạn đã đánh giá rồi", HttpStatus.CONFLICT),

	ALREADY_REPORTED(1041, "Bạn đã report rồi", HttpStatus.CONFLICT),

	ALREADY_FRIEND(1042, "Hai người đã là bạn bè", HttpStatus.CONFLICT),

	CANNOT_FOLLOW_YOURSELF(1043, "Không thể theo dõi chính mình", HttpStatus.BAD_REQUEST),

	LOGIN_FAILED(1044, "Đăng nhập thất bại", HttpStatus.UNAUTHORIZED),

	FAILED_TO_PARSE_DATA(1045, "Không thể phân tích dữ liệu", HttpStatus.BAD_REQUEST),

	INVALID_JSON_FORMAT(1046, "Dữ liệu JSON không hợp lệ", HttpStatus.BAD_REQUEST),

	MESSAGE_SEND_FAILED(1048, "Gửi tin nhắn thất bại", HttpStatus.BAD_REQUEST);

	private final int code;
	private final String message;
	private final HttpStatusCode statusCode;

}
package com.example.app.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLessonResponse {
	private Long id;
	private String content;
	private LocalDateTime createdAt;
	private Long idParent;
	private LocalDateTime updatedAt;
	private Long userId;
	private String username;
	private String userAvatar;
	private Long LessonId;
	private boolean hide;
}

package com.example.app.dto.response.comment;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
	private Long id;
	private String content;
	private LocalDateTime createdAt;
	private Long parentId;
	private LocalDateTime updatedAt;
	private Long userId;
	private String username;
	private String userAvatar;
	private Long contentId;
	private Long level;
	private boolean hide;
}

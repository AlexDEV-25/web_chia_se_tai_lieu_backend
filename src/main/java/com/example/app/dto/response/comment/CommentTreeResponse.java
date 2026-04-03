package com.example.app.dto.response.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.app.share.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentTreeResponse {
	private Long id;
	private String content;
	private LocalDateTime createdAt;
	private Long idParent;
	private LocalDateTime updatedAt;
	private Long userId;
	private String username;
	private String userAvatar;
	private Long ContentId;
	private Long level;
	private boolean hide;
	private Type type;
	private List<CommentTreeResponse> children = new ArrayList<>();
}

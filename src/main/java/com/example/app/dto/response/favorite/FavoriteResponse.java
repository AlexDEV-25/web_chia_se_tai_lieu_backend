package com.example.app.dto.response.favorite;

import java.time.LocalDateTime;

import com.example.app.share.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
	private Long id;
	private LocalDateTime createdAt;
	private Long contentId;
	private String title;
	private String thumbnailUrl;
	private String authorName;
	private Type type;
}

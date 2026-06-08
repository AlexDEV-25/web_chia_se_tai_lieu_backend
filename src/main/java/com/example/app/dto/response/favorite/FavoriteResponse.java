package com.example.app.dto.response.favorite;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {
	private Long id;
	private LocalDateTime createdAt;
	private Long contentId;
	private String title;
	private String thumbnailUrl;
	private String authorName;
}

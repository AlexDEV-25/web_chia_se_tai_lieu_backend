package com.example.app.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
	private Long id;
	private Integer rating;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long userId;
	private Long documentId;
}

package com.example.app.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
	private Integer rating;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Long documentId;
	private Long userId;
}

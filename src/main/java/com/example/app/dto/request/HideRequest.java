package com.example.app.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HideRequest {
	private boolean hide;
	private LocalDateTime updatedAt = LocalDateTime.now();
}

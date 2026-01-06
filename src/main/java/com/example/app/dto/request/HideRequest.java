package com.example.app.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HideRequest {

	@NotNull(message = "hide không được để trống")
	private boolean hide;

	@NotNull(message = "updatedAt không được để trống")
	private LocalDateTime updatedAt;
}

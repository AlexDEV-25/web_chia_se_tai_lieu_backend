package com.example.app.dto.request;

import com.example.app.constant.InteractionType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteRequest {

	@NotNull(message = "contentId không được để trống")
	private Long contentId;

	@NotNull(message = "Type không được null")
	private InteractionType type;
}

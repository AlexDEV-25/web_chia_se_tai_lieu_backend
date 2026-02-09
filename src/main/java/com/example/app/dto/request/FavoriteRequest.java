package com.example.app.dto.request;

import com.example.app.share.Type;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequest {

	@NotNull(message = "contentId không được để trống")
	private Long contentId;

	@NotNull(message = "Type không được null")
	private Type type;
}

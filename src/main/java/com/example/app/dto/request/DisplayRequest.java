package com.example.app.dto.request;

import com.example.app.constant.HideType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayRequest {

	@NotNull(message = "hide không được để trống")
	private boolean hide;

	@NotNull(message = "Type không được null")
	private HideType type;
}

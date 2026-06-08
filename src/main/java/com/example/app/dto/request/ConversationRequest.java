package com.example.app.dto.request;

import java.util.List;

import com.example.app.constant.ConversationType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConversationRequest {
	@NotNull(message = "type không được null")
	private ConversationType type;

	@Size(min = 1)
	@NotNull(message = "list không được null")
	private List<Long> participantIds;

}

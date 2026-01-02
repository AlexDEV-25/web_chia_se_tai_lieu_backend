package com.example.app.dto.request;

import java.time.LocalDateTime;

import com.example.app.share.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusRequest {
	private Status status;
	private LocalDateTime updatedAt;
}

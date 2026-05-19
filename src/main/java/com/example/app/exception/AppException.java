package com.example.app.exception;

import com.example.app.constant.AppError;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private AppError appError;

}

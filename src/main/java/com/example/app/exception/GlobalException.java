package com.example.app.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

	@ExceptionHandler(RuntimeException.class)
	ResponseEntity<String> handlingRuntimeException(RuntimeException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<String> handlingException(Exception e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(AppException.class)
	ResponseEntity<String> handlingAppException(AppException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}
